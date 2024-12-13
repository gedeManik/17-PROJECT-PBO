
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConn {
    private static final String URL = "jdbc:mysql://localhost:3306/spacewar";
    private static final String USER = "root";
    private static final String PASSWORD = "manik";

    // Method to retrieve data from the database
    // public static List<Player> getPlayersFromDatabase() {
    //     List<Player> players = new ArrayList<>();
    //     try {
    //         // Load MySQL JDBC Driver
    //         Class.forName("com.mysql.cj.jdbc.Driver");

    //         // Establish connection to the database
    //         Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

    //         // Create a statement to execute the query
    //         Statement statement = connection.createStatement();
    //         ResultSet resultSet = statement.executeQuery("SELECT * FROM spacewar");

    //         // Iterate over the result set and populate the list
    //         while (resultSet.next()) {
    //             int id = resultSet.getInt("ID");
    //             String username = resultSet.getString("username");
    //             int score = resultSet.getInt("score");

    //             players.add(new Player(id, username, score));
    //         }

    //         // Close the connection
    //         connection.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return players;
    // }

    public static void savePlayerToDatabase(String username, int score) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "INSERT INTO spacewar (username, score) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, score);

            preparedStatement.executeUpdate();
            connection.close();

            System.out.println("Player data saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores(int limit) {
        List<String> topScores = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Membuka koneksi ke database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Query SQL untuk mengambil skor tertinggi
            String query = "SELECT username, score FROM spacewar ORDER BY score DESC LIMIT ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            resultSet = preparedStatement.executeQuery();

            // Iterasi melalui hasil dan tambahkan ke daftar
            while (resultSet.next()) {
                String playerName = resultSet.getString("username");
                int score = resultSet.getInt("score");
                topScores.add(playerName + " - " + score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return topScores;
    }

    



}



// package src;

// import java.sql.Connection;
// import java.sql.DriverManager;

// public class DatabaseConn {
//     public static void main(String[] args) {
//         String URL = "jdbc:mysql://localhost:3306/spacewar";
//         String USER = "root";
//         String PASSWORD = "manik"; //sesuaikan dengan password masing masing

//         try {
//             Class.forName("com.mysql.cj.jdbc.Driver");
//             Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//             System.out.println("Connected to the database!");
//             connection.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
