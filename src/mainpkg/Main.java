package mainpkg;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int avgLongest = 0, avgRes = 0, avgShortest=0,
                avgItereations = 0, tries = 3,
                bestLongest=0,bestShortest=Integer.MAX_VALUE;
        long avgTime = 0;

        //test
        //int[][] distances = getDistances();
        //save(distances);

        int[][] distances = new int[300][300];
        restore(distances);
        //System.out.println(Arrays.deepToString(distances));

        for (int i = 0; i < tries; i++) {
            long startTime = System.currentTimeMillis();
            //ACO ACO = new ACO(getDistances());

            //test
            ACO ACO = new ACO(distances);

            ACO.generateNNList();
            ACO.generateAnts();
            ACO.generateEnvironment();

            int result = 0;
            int longest = 0, shortest=Integer.MAX_VALUE, iterations = 0;

            int n = 0;
            while (n < Constants.iterations) {
                ACO.constructSolutions();
                ACO.updatePheromone();

                Ant[] ants = ACO.getAnts();
                double[] tourCosts = Arrays.stream(ants).mapToDouble(Ant::getTourCost).sorted().toArray();//i -> array[array.length - i]
                result += tourCosts[0];

                /*
                for (double tourCost : tourCosts) {
                    System.out.print(tourCost);
                }
                System.out.println("\n \n \n");
                 */

                if (tourCosts[tourCosts.length-1] > longest) {
                    longest = (int) tourCosts[tourCosts.length-1];
                    if (bestLongest < longest) {
                        bestLongest = longest;
                    }
                }
                if(tourCosts[0]<shortest){
                    shortest=(int) tourCosts[0];
                    if(bestShortest>shortest)
                        bestShortest=shortest;
                    iterations = n + 1;
                }

                n++;
            }
            avgItereations += iterations;
            result = result / Constants.iterations;
            avgRes += result;
            avgLongest += longest;
            avgShortest+=shortest;
            avgTime += System.currentTimeMillis() - startTime;
            //System.out.println("avg: " + result);
            System.out.println(i + 1 + ") Average result: " + result + " | Longest: " + longest +
                    " | Shortest: " + shortest +
                    " found after " + iterations + " iteration(s)");
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Average result:" + avgRes / tries +
                " | Average longest: " + avgLongest / tries +
                "|  Average shortest: " + avgShortest / tries +
                " | average iterations: " + avgItereations / tries +
                "\nShortest: " + bestShortest +
                "\nLongest: " + bestLongest +
                "\nAverage time: " + avgTime / tries + " ms\n");
    }

    public static int[][] getDistances() {
        int[][] distances = new int[300][300];
        fillDistances(distances);
        return distances;
    }

    private static void fillDistances(int[][] distances) {
        for (int i = 0; i < distances.length; i++) {
            for (int j = i; j < distances.length; j++) {
                if (i == j)
                    continue;
                Random random = new Random();
                distances[i][j] = random.nextInt(145) + 5;
                distances[i][j] += (double) (Math.round(random.nextDouble() * 300)) / 300;
                distances[j][i] = distances[i][j];
            }
        }
    }


    public static void save(int[][] distances) {
        try {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < distances.length; i++)
            {
                for (int j = 0; j < distances.length; j++)
                {
                    builder.append(distances[i][j] + "");
                    if (j < distances.length - 1)
                        builder.append(",");
                }
                builder.append("\n");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void restore(int[][] distances) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            String line = "";
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                int col = 0;
                for (String c : cols) {
                    distances[row][col] = Integer.parseInt(c);
                    col++;
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
