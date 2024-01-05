package org.example.rule.visualization.endpoint;

import org.drools.ansible.rulebook.integration.api.RuleFormat;
import org.drools.ansible.rulebook.integration.api.RuleNotation;
import org.drools.ansible.rulebook.integration.api.domain.RulesSet;
import org.drools.ansible.rulebook.integration.visualization.parser.RulesSetParser;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.json.GraphJsonGenerator;
import org.drools.impact.analysis.model.AnalysisModel;

public enum RulesVisualizerService {
    INSTANCE;

    public String graphAsJson(String payload) {
        RulesSet rulesSet = RuleNotation.CoreNotation.INSTANCE.toRulesSet(RuleFormat.JSON, payload);
        AnalysisModel analysisModel = RulesSetParser.parse(rulesSet);
        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        return GraphJsonGenerator.generateJson(graph);
    }
}
