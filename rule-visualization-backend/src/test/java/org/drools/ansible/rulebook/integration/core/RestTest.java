
package org.drools.ansible.rulebook.integration.core;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RestTest {

    private static final String RULES_JSON = """
            {
              "name":"05 Post event",
              "hosts":[
                "all"
              ],
              "rules":[
                {
                  "Rule":{
                    "name":"r1",
                    "condition":{
                      "AllCondition":[
                        {
                          "EqualsExpression":{
                            "lhs":{
                              "Event":"i"
                            },
                            "rhs":{
                              "Integer":1
                            }
                          }
                        }
                      ]
                    },
                    "actions":[
                      {
                        "Action":{
                          "action":"post_event",
                          "action_args":{
                            "event":{
                              "msg":"hello world"
                            }
                          }
                        }
                      }
                    ],
                    "enabled":true
                  }
                },
                {
                  "Rule":{
                    "name":"r2",
                    "condition":{
                      "AllCondition":[
                        {
                          "EqualsExpression":{
                            "lhs":{
                              "Event":"msg"
                            },
                            "rhs":{
                              "String":"hello world"
                            }
                          }
                        }
                      ]
                    },
                    "actions":[
                      {
                        "Action":{
                          "action":"set_fact",
                          "action_args": {
                            "fact": {
                              "status": "created"
                            }
                          }
                        }
                      }
                    ],
                    "enabled":true
                  }
                },
                {
                  "Rule":{
                    "name":"r3",
                    "condition":{
                      "AllCondition":[
                        {
                          "EqualsExpression":{
                            "lhs":{
                              "Event":"status"
                            },
                            "rhs":{
                              "String":"created"
                            }
                          }
                        }
                      ]
                    },
                    "actions":[
                      {
                        "Action":{
                          "action":"debug",
                          "action_args":{
                          }
                        }
                      }
                    ],
                    "enabled":true
                  }
                },
                {
                  "Rule":{
                    "name":"r4",
                    "condition":{
                      "AllCondition":[
                        {
                          "EqualsExpression":{
                            "lhs":{
                              "Event":"i"
                            },
                            "rhs":{
                              "Integer":2
                            }
                          }
                        }
                      ]
                    },
                    "actions":[
                      {
                        "Action":{
                          "action":"retract_fact",
                          "action_args": {
                            "fact": {
                              "status": "created"
                            }
                          }
                        }
                      }
                    ],
                    "enabled":true
                  }
                }
              ]
            }
                        """;

    private static final String EXPECTED_TOPOLOGY_JSON = """
            {
              "NODES":[
                {
                  "id":"defaultpkg.r1",
                  "label":"r1",
                  "type":"node"
                },
                {
                  "id":"defaultpkg.r2",
                  "label":"r2",
                  "type":"node"
                },
                {
                  "id":"defaultpkg.r3",
                  "label":"r3",
                  "type":"node"
                },
                {
                  "id":"defaultpkg.r4",
                  "label":"r4",
                  "type":"node"
                }
              ],
              "EDGES":[
                {
                  "id":"edge-defaultpkg.r1-defaultpkg.r2",
                  "source":"defaultpkg.r1",
                  "type":"edge",
                  "edgeStyle":"solid",
                  "target":"defaultpkg.r2"
                },
                {
                  "id":"edge-defaultpkg.r2-defaultpkg.r3",
                  "source":"defaultpkg.r2",
                  "type":"edge",
                  "edgeStyle":"solid",
                  "target":"defaultpkg.r3"
                },
                {
                  "id":"edge-defaultpkg.r4-defaultpkg.r3",
                  "source":"defaultpkg.r4",
                  "type":"edge",
                  "edgeStyle":"dashed",
                  "target":"defaultpkg.r3"
                }
              ]
            }
            """;

    @Test
    public void generateTopologyJson() throws JsonProcessingException {
        Response response = given()
                .body(RULES_JSON)
                .contentType(ContentType.JSON)
                .when()
                .post("/rules-visualizer")
                .then()
                .extract().response();

        assertJson(response.asString(), EXPECTED_TOPOLOGY_JSON);
    }

    private static void assertJson(String actualJson, String expectedJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Map<String, String>>> actualMap = objectMapper.readValue(actualJson, Map.class);
        Map<String, List<Map<String, String>>> expectedMap = objectMapper.readValue(expectedJson, Map.class);

        List<Map<String, String>> actualNodes = actualMap.get("NODES");
        List<Map<String, String>> expectedNodes = expectedMap.get("NODES");
        assertThat(actualNodes).containsExactlyInAnyOrderElementsOf(expectedNodes);

        List<Map<String, String>> actualEdges = actualMap.get("EDGES");
        List<Map<String, String>> expectedEdges = expectedMap.get("EDGES");
        assertThat(actualEdges).containsExactlyInAnyOrderElementsOf(expectedEdges);
    }
}
