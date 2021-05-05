import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class BaseballElimination {

    private final int numberOfTeams;
    private final TreeMap<String, Integer> teams = new TreeMap<>();
    private final TreeMap<Integer, String> invertedTeamsMap = new TreeMap<>();
    private final int[] wins;
    private final int[] loss;
    private final int[] remaining;
    private final int[][] games;
    private final TreeMap<String, TeamEliminationStats> teamEliminationStatsTreeMap
            = new TreeMap<>();


    public BaseballElimination(
            String filename)  // create a baseball division from given filename in format specified below
    {
        In in = new In(filename);
        numberOfTeams = in.readInt();
        wins = new int[numberOfTeams];
        loss = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        games = new int[numberOfTeams][numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            String teamName = in.readString();
            teams.put(teamName, i);
            invertedTeamsMap.put(i, teamName);
            wins[i] = in.readInt();
            loss[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < numberOfTeams; j++) {
                games[i][j] = in.readInt();
            }
        }
        // for (int i = 0; i < numberOfTeams; i++) {
        //     StringBuilder str =  new StringBuilder();
        //     str.append(invertTeamsMap.get(i)+" "+wins[i]+" "+loss[i]+" "+remaining[i]+" ");
        //     for (int j = 0; j < numberOfTeams; j++) {
        //         str.append(" "+games[i][j]+ " ");
        //     }
        //     System.out.println(str.toString());
        // }
        in.close();
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    public boolean isEliminated(String team)              // is given team eliminated?
    {
        checkTeamIsValid(team);
        return teamEliminationStatsTreeMap.computeIfAbsent(team, this::isTeamEliminated)
                                          .isEliminated();
    }

    public Iterable<String> certificateOfElimination(
            String team)  // subset R of teams that eliminates given team; null if not eliminated
    {
        checkTeamIsValid(team);
        if (!isEliminated(team)) return null;
        return teamEliminationStatsTreeMap.computeIfAbsent(team, this::isTeamEliminated)
                                          .getCertificateOfElimination();
    }

    private TeamEliminationStats isTeamEliminated(String team) {
        int maxWins = wins(team) + remaining(team);
        for (String otherTeam : teams()) {
            if ((!otherTeam.equals(team)) && (wins(otherTeam) > maxWins))
                return new TeamEliminationStats(true, Collections.singletonList(otherTeam));
        }
        return isNonTriviallyEliminated(team);
    }

    private TeamEliminationStats isNonTriviallyEliminated(String team) {
        int gameVertices = ((numberOfTeams() - 2) * (numberOfTeams() - 1) / 2);
        int teamVertices = (numberOfTeams() - 1);
        int v = 2 + teamVertices + gameVertices;
        final int SOURCE = 0;
        final int SINK = v - 1;
        int maxWins = wins(team) + remaining(team);
        int totalLeftGames = 0;
        FlowNetwork flowNetwork = new FlowNetwork(v);
        TreeMap<String, Integer> gameVerticesMap = createGameVerticesMap(team);
        TreeMap<String, Integer> teamVerticesMap = createTeamVerticesMap(team);
        for (String game : gameVerticesMap.keySet()) {
            String[] gameTeams = game.split("-");
            assert gameTeams.length == 2 : "key error in split";
            flowNetwork.addEdge(new FlowEdge(SOURCE, gameVerticesMap.get(game),
                                             against(gameTeams[0], gameTeams[1])));
            flowNetwork.addEdge(
                    new FlowEdge(gameVerticesMap.get(game), teamVerticesMap.get(gameTeams[0]),
                                 Double.POSITIVE_INFINITY));
            flowNetwork.addEdge(
                    new FlowEdge(gameVerticesMap.get(game), teamVerticesMap.get(gameTeams[1]),
                                 Double.POSITIVE_INFINITY));
            totalLeftGames += against(gameTeams[0], gameTeams[1]);
        }
        for (String teamName : teamVerticesMap.keySet()) {
            flowNetwork.addEdge(
                    new FlowEdge(teamVerticesMap.get(teamName), SINK, maxWins - wins(teamName)));
        }
        assert flowNetwork.V() == v : "mismatch of vertices in flow network";
        assert flowNetwork.E() == (3 * gameVertices) + teamVertices :
                "mismatch of edges in flow network";
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, SOURCE, SINK);
        if (fordFulkerson.value() == totalLeftGames) return new TeamEliminationStats(false, null);
        ArrayList<String> certificateOfElimination = new ArrayList<>();
        for (String teamName : teams()) {
            if (teamName.equals(team)) continue;
            if (fordFulkerson.inCut(teamVerticesMap.get(teamName)))
                certificateOfElimination.add(teamName);
        }
        return new TeamEliminationStats(true, certificateOfElimination);
    }

    private TreeMap<String, Integer> createGameVerticesMap(String team) {
        checkTeamIsValid(team);
        int teamId = teams.get(team);
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        int virtualId = 1;
        for (int i = 0; i < numberOfTeams(); i++) {
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if ((teamId == i) || (teamId == j)) continue;
                treeMap.put(invertedTeamsMap.get(i) + "-" + invertedTeamsMap.get(j), virtualId++);
            }
        }
        int gameVertices = ((numberOfTeams() - 2) * (numberOfTeams() - 1) / 2);
        assert gameVertices == treeMap.size() :
                "mismatch of game vertices count: " + gameVertices + "-" + treeMap.size();
        return treeMap;
    }

    private TreeMap<String, Integer> createTeamVerticesMap(String team) {
        int gameVertices = ((numberOfTeams() - 2) * (numberOfTeams() - 1) / 2);
        checkTeamIsValid(team);
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        int virtualId = 1 + gameVertices;
        for (String teamName : teams()) {
            if (team.equals(teamName)) continue;
            treeMap.put(teamName, virtualId++);
        }
        int teamVertices = (numberOfTeams() - 1);
        assert treeMap.size() == teamVertices : "mismatch in team count";
        return treeMap;
    }

    public int numberOfTeams()                        // number of teams
    {
        return numberOfTeams;
    }

    public Iterable<String> teams()                                // all teams
    {
        return teams.keySet();
    }

    public int wins(String team)                      // number of wins for given team
    {
        checkTeamIsValid(team);
        return wins[teams.get(team)];
    }

    public int remaining(String team)                 // number of remaining games for given team
    {
        checkTeamIsValid(team);
        return remaining[teams.get(team)];
    }

    public int losses(String team)                    // number of losses for given team
    {
        checkTeamIsValid(team);
        return loss[teams.get(team)];
    }

    private void checkTeamIsValid(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
    }

    public int against(String team1,
                       String team2)    // number of remaining games between team1 and team2
    {
        checkTeamIsValid(team1);
        checkTeamIsValid(team2);
        return games[teams.get(team1)][teams.get(team2)];
    }


    private class TeamEliminationStats {
        private final boolean isEliminated;
        private final List<String> certificateOfElimination;

        public TeamEliminationStats(boolean isEliminated, List<String> certificateOfElimination) {
            this.isEliminated = isEliminated;
            if (certificateOfElimination == null) {
                this.certificateOfElimination = null;
            }
            else {
                this.certificateOfElimination = new ArrayList<>(certificateOfElimination);
            }
        }

        public boolean isEliminated() {
            return isEliminated;
        }

        public List<String> getCertificateOfElimination() {
            if (certificateOfElimination == null) {
                return null;
            }
            return new ArrayList<>(certificateOfElimination);
        }
    }
}