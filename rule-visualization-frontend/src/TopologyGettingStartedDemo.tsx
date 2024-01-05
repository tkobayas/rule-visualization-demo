import * as React from 'react';
import {
  ColaLayout,
  ComponentFactory,
  DefaultEdge,
  DefaultGroup,
  DefaultNode,
  EdgeStyle,
  Graph,
  GraphComponent,
  Layout,
  LayoutFactory,
  Model,
  ModelKind,
  NodeShape,
  SELECTION_EVENT,
  Visualization,
  VisualizationProvider,
  VisualizationSurface,
  nodeDragSourceSpec,
  withDragNode
} from '@patternfly/react-topology';

import "@patternfly/react-core/dist/styles/base.css";

const baselineLayoutFactory: LayoutFactory = (type: string, graph: Graph): Layout | undefined => {
  switch (type) {
    case 'Cola':
      return new ColaLayout(graph);
    default:
      return new ColaLayout(graph, { layoutOnDrag: false });
  }
};

const baselineComponentFactory: ComponentFactory = (kind: ModelKind, type: string) => {
  switch (type) {
    case 'group':
      return DefaultGroup;
    default:
      switch (kind) {
        case ModelKind.graph:
          return GraphComponent;
        case ModelKind.node:
          return withDragNode(nodeDragSourceSpec('node', true, true))(DefaultNode);
        case ModelKind.edge:
          return DefaultEdge;
        default:
          return undefined;
      }
  }
};

const NODE_SHAPE = NodeShape.ellipse;
const NODE_DIAMETER = 75;

interface Node {
  id: string;
  type: string;
  label: string;
  width: number;
  height: number;
  shape: NodeShape;
}

interface Edge {
  id: string;
  type: string;
  source: string;
  target: string;
  edgeStyle: EdgeStyle;
}

// sample rules
const rulesJsonText = `{"name":"05 Post event","hosts":["all"],
"rules":
[
  {"Rule":{"name":"r1",
    "condition":{"AllCondition":[{"EqualsExpression":{"lhs":{"Event":"i"},"rhs":{"Integer":1}}}]},
    "actions":[{"Action":{"action":"post_event","action_args":{"event":{"msg":"hello world"}}}}],"enabled":true}},
  {"Rule":{"name":"r2",
    "condition":{"AllCondition":[{"EqualsExpression":{"lhs":{"Event":"msg"},"rhs":{"String":"hello world"}}}]},
    "actions":[{"Action":{"action":"set_fact","action_args":{"fact":{"status":"created"}}}}],"enabled":true}},
  {"Rule":{"name":"r3",
    "condition":{"AllCondition":[{"EqualsExpression":{"lhs":{"Event":"status"},"rhs":{"String":"created"}}}]},
    "actions":[{"Action":{"action":"debug","action_args":{}}}],"enabled":true}},
  {"Rule":{"name":"r4",
    "condition":{"AllCondition":[{"EqualsExpression":{"lhs":{"Event":"i"},"rhs":{"Integer":2}}}]},
    "actions":[{"Action":{"action":"retract_fact","action_args":{"fact":{"status":"created"}}}}],"enabled":true}}
]
}`;

export const TopologyGettingStartedDemo: React.FC = () => {
  const [nodes, setNodes] = React.useState<Node[]>([]);
  const [edges, setEdges] = React.useState<Edge[]>([]);

  const [loading, setLoading] = React.useState(true); 

  React.useEffect(() => {
    setLoading(true);
    // Send a rules definition JSON and get back a topology JSON
    fetch('http://localhost:8080/rules-visualizer', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
      },
      body: rulesJsonText,
    })
      .then(response => response.json())
      .then(data => {
        const updatedNodes = data.NODES.map((node : Node) => ({
          ...node,
          width: NODE_DIAMETER,
          height: NODE_DIAMETER,
          shape: NODE_SHAPE
        }));

        const updatedEdges = data.EDGES.map((edge: Edge) => ({
          ...edge,
          edgeStyle: edge.edgeStyle === 'solid' ? EdgeStyle.solid : EdgeStyle.dashed // Add other conditions as needed
        }));
        
        // Update state with the modified nodes and edges
        setNodes(updatedNodes);
        setEdges(updatedEdges);

        setLoading(false);
      })
      .catch(error => {
        console.error("Error fetching data: ", error);
      });
  }, []); // The empty array ensures this effect runs only once

  console.log("nodes: ", nodes);
  console.log("edges: ", edges);

  const [selectedIds, setSelectedIds] = React.useState<string[]>([]);

  const controller = React.useMemo(() => {
    const model: Model = {
      nodes: nodes,
      edges: edges,
      graph: {
        id: 'g1',
        type: 'graph',
        layout: 'Cola'
      }
    };

    const newController = new Visualization();
    newController.registerLayoutFactory(baselineLayoutFactory);
    newController.registerComponentFactory(baselineComponentFactory);

    newController.addEventListener(SELECTION_EVENT, setSelectedIds);

    newController.fromModel(model, false);

    return newController;
  }, [nodes, edges]);

  if (loading) {
    return <div>Loading...</div>; // or any loading indicator you prefer
  }

  return (
    <VisualizationProvider controller={controller}>
      <VisualizationSurface state={{ selectedIds }} />
    </VisualizationProvider>
  );
};
