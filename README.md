# Rule Visualization Demo

1. Run rule-visualization-backend
```
Open a terminal
$ cd rule-visualization-backend
$ mvn clean compile quarkus:dev
```

2. Run rule-visualization-frontend
```
Open a terminal
$ cd rule-visualization-frontend
$ npm start
```

It opens [http://localhost:3000](http://localhost:3000) with a browser.

The frontend posts a rules definition JSON to the backend service and get a topology JSON response.

The frontend renders the topology JSON.
