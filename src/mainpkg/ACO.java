package mainpkg;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ACO {
    private final int[][] graph;
    private int[][] NNList;
    private double[][] pheromone;
    private double[][] choice;
    private Ant[] ants;

    public ACO(int[][] graph) {
        super();
        this.graph = graph;
    }

    public void generateNNList() {
        NNList = new int[getNodesSize()][getNNSize()];

        for (int i = 0; i < getNodesSize(); i++) {
            Integer[] nodeIndex = new Integer[getNodesSize()];
            Double[] nodeData = new Double[getNodesSize()];
            for (int j = 0; j < getNodesSize(); j++) {
                nodeIndex[j] = j;
                nodeData[j] = getCost(i, j);
            }

            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(nodeIndex, Comparator.comparingDouble(o -> nodeData[o]));
            for (int r = 0; r < getNNSize(); r++) {
                NNList[i][r] = nodeIndex[r];
            }
        }
    }

    public void generateAnts() {
        ants = new Ant[getAntAmount()];
        for (int k = 0; k < getAntAmount(); k++) {
            ants[k] = new Ant(getNodesSize(), this);
        }
    }

    public void generateEnvironment() {
        pheromone = new double[getNodesSize()][getNodesSize()];
        choice = new double[getNodesSize()][getNodesSize()];
        double initialTrail = 1.0 / (Constants.p * ants[0].calcNNTour());
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = initialTrail;
                pheromone[j][i] = initialTrail;
                choice[i][j] = initialTrail;
                choice[j][i] = initialTrail;
            }
        }
        calcChoice();
    }

    public void calcChoice() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCost(i, j) + 0.1));
                choice[i][j] = Math.pow(pheromone[i][j], Constants.alpha) * Math.pow(heuristic, Constants.beta);
                choice[j][i] = choice[i][j];
            }
        }
    }

    public void constructSolutions() {
        int phase = 0;
        for (int k = 0; k < getAntAmount(); k++) {
            ants[k].clearVisited();
            ants[k].startAtRandom(phase);
        }

        while (phase < getNodesSize() - 1) {
            phase++;
            for (int k = 0; k < getAntAmount(); k++) {
                ants[k].move(phase);
            }
        }

        for (int k = 0; k < getAntAmount(); k++) {
            ants[k].finishTour();
        }
    }

    public void updatePheromone() {
        evaporatePheromone();
        for (int k = 0; k < getAntAmount(); k++) {
            depositPheromone(ants[k]);
        }
        calcChoice();
    }

    public void evaporatePheromone() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = (1 - Constants.p) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    public void depositPheromone(Ant ant) {
        double dTau = 1.0 / ant.getTourCost();
        for (int i = 0; i < getNodesSize(); i++) {
            int j = ant.getRoutePhase(i);
            int l = ant.getRoutePhase(i + 1);
            pheromone[j][l] = pheromone[j][l] + dTau;
            pheromone[l][j] = pheromone[j][l];
        }
    }
    public void printGraph(ACO ACO) {
        int[][] graph = ACO.getGraph();
        for (int[] ints : graph) {
            for (int aInt : ints) {
                System.out.print(aInt + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getNodesSize() {
        return graph.length;
    }

    public int getNNSize() {
        return 20;
    }

    public double getCost(int from, int to) {
        return graph[from][to];
    }

    public int getAntAmount() {
        return Constants.ants;
    }

    public int getNNNode(int from, int index) {
        return this.NNList[from][index];
    }

    public double getCostInfo(int from, int to) {
        return choice[from][to];
    }

    public Ant[] getAnts(){
        return ants;
    }

    public int[][] getGraph(){
        return graph;
    }
}