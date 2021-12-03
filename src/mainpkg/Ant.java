package mainpkg;

import java.util.Arrays;

public class Ant {
    private double tourCost;
    private final int[] tour;
    private final boolean[] visited;
    private final ACO ACO;

    public Ant(int tourSize, ACO ACO) {
        super();
        this.tour = new int[tourSize + 1];
        this.visited = new boolean[tourSize];
        this.ACO = ACO;
    }

    public double calcNNTour() {
        int phase = 0;
        clearVisited();
        startAtRandom(phase);
        while (phase < ACO.getNodesSize() - 1) {
            phase++;
            goToBestNext(phase);
        }
        finishTour();
        clearVisited();
        return this.tourCost;
    }

    public void clearVisited() {
        Arrays.fill(visited, false);
    }

    public void startAtRandom(int phase) {
        tour[phase] = (int) (Math.random() * ACO.getNodesSize());
        visited[tour[phase]] = true;
    }

    public void goToBestNext(int phase) {
        int nextCity = ACO.getNodesSize();

        int currentCity = tour[phase - 1];

        double minDistance = Double.MAX_VALUE;

        for (int city = 0; city < ACO.getNodesSize(); city++) {
            if (!visited[city] && ACO.getCost(currentCity, city) < minDistance) {
                nextCity = city;
                minDistance = ACO.getCost(currentCity, city);
            }
        }

        tour[phase] = nextCity;
        visited[nextCity] = true;
    }

    public double calcTourCost() {
        double tourCost = 0.0;
        for (int i = 0; i < ACO.getNodesSize(); i++) {
            tourCost += ACO.getCost(tour[i], tour[i + 1]);
        }
        return tourCost;
    }

    public void finishTour() {
        tour[ACO.getNodesSize()] = tour[0];
        tourCost = calcTourCost();
    }

    public void move(int phase) {
        int currentCity = this.tour[phase - 1];
        double sumProbabilities = 0.0;

        double[] selectionProbabilities = new double[ACO.getNNSize() + 1];

        for (int j = 0; j < ACO.getNNSize(); j++) {
            if (visited[ACO.getNNNode(currentCity, j)]) {
                selectionProbabilities[j] = 0.0;
            } else {
                selectionProbabilities[j] = ACO.getCostInfo(currentCity, ACO.getNNNode(currentCity, j));
                sumProbabilities += selectionProbabilities[j];
            }
        }
        if (sumProbabilities <= 0) {
            goToBestNext(phase);
        } else {
            double rand = Math.random() * sumProbabilities;
            int j = 0;
            double probability = selectionProbabilities[j];

            while (probability <= rand || j < ACO.getNNSize()) {
                /*???*/
                j++;
                if(j<ACO.getNNSize()&&j>=0)
                    probability += selectionProbabilities[j];
            }

            if (j == ACO.getNNSize()) {
                goToBestNeighbor(phase);
                return;
            }

            tour[phase] = ACO.getNNNode(currentCity, j);
            visited[this.tour[phase]] = true;
        }
    }

    public void goToBestNeighbor(int phase) {
        int helpCity;
        int nextCity = ACO.getNodesSize();

        int currentCity = this.tour[phase - 1];

        double valueBest = -1.0;
        double help;

        for (int i = 0; i < ACO.getNNSize(); i++) {
            helpCity = ACO.getNNNode(currentCity, i);
            if (!this.visited[helpCity]) {
                help = ACO.getCostInfo(currentCity, helpCity);
                if (help > valueBest) {
                    valueBest = help;
                    nextCity = helpCity;
                }
            }
        }
        if (nextCity == ACO.getNodesSize()) {
            goToBestNext(phase);
        } else {
            tour[phase] = nextCity;
            visited[this.tour[phase]] = true;
        }
    }

    public double getTourCost() {
        return tourCost;
    }

    public int getRoutePhase(int phase) {
        return tour[phase];
    }
}