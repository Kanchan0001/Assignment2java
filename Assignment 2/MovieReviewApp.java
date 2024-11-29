import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class MovieReviewApp {
    private static final String API_KEY = "8801ba0f";  // Replace with your API key

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JTextField movieTitleField;
    private JLabel resultLabel;
    private JLabel genreLabel;
    private JLabel directorLabel;
    private JLabel yearLabel;
    private JLabel ratingsLabel;
    private JLabel posterLabel;
    private JButton viewPosterButton;  // Reference to "View Poster" button

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovieReviewApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Movie Review App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        // CardLayout for navigation between pages
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Main Page Components
        JPanel mainPage = new JPanel();
        mainPage.setLayout(new BoxLayout(mainPage, BoxLayout.Y_AXIS));
        mainPage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Enter Movie Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        movieTitleField = new JTextField(15);  // Smaller size for the text field
        movieTitleField.setFont(new Font("Arial", Font.PLAIN, 14));
        movieTitleField.setMaximumSize(movieTitleField.getPreferredSize());  // Limit width of the text field

        JButton searchButton = new JButton("Search Movie");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(0x4CAF50));  // Green background
        searchButton.setForeground(Color.WHITE);

        resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setForeground(Color.BLUE);

        genreLabel = new JLabel();
        directorLabel = new JLabel();
        yearLabel = new JLabel();
        ratingsLabel = new JLabel();

        viewPosterButton = new JButton("View Poster");  // Create the "View Poster" button
        viewPosterButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewPosterButton.setBackground(new Color(0x2196F3));  // Blue background
        viewPosterButton.setForeground(Color.WHITE);
        viewPosterButton.setVisible(false);  // Initially hidden

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String movieTitle = movieTitleField.getText().trim();
                if (!movieTitle.isEmpty()) {
                    fetchMovieDetails(movieTitle);
                } else {
                    resultLabel.setText("Please enter a valid movie title.");
                }
            }
        });

        // Main page layout
        mainPage.add(titleLabel);
        mainPage.add(movieTitleField);
        mainPage.add(searchButton);
        mainPage.add(resultLabel);
        mainPage.add(genreLabel);
        mainPage.add(directorLabel);
        mainPage.add(yearLabel);
        mainPage.add(ratingsLabel);
        mainPage.add(viewPosterButton);  // Add the "View Poster" button to the main page

        // Button to navigate to poster page
        viewPosterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "posterPage");
            }
        });

        // Poster Page Components
        JPanel posterPage = new JPanel();
        posterPage.setLayout(new BorderLayout());
        posterLabel = new JLabel();
        posterPage.add(posterLabel, BorderLayout.CENTER);

        // Back button to return to main page
        JButton backButton = new JButton("Back to Main");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(0xF44336));  // Red background
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "mainPage");
            }
        });

        // Add the back button to the poster page
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        posterPage.add(backPanel, BorderLayout.SOUTH);

        // Add both pages to cardPanel
        cardPanel.add(mainPage, "mainPage");
        cardPanel.add(posterPage, "posterPage");

        frame.add(cardPanel);
        frame.setVisible(true);
    }

    private void fetchMovieDetails(String movieTitle) {
        try {
            // Build the API request URL
            String url = "https://www.omdbapi.com/?t=" + movieTitle.replace(" ", "+") + "&apikey=" + API_KEY;

            // Create HTTP connection
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("Response") && jsonResponse.getString("Response").equals("True")) {
                // Extract details
                String title = jsonResponse.getString("Title");
                String year = jsonResponse.getString("Year");
                String genre = jsonResponse.getString("Genre");
                String director = jsonResponse.getString("Director");
                JSONArray ratings = jsonResponse.optJSONArray("Ratings");
                String posterUrl = jsonResponse.getString("Poster");

                // Display details in UI
                resultLabel.setText("<html><b>Title:</b> " + title + "</html>");
                genreLabel.setText("<html><b>Genre:</b> " + genre + "</html>");
                directorLabel.setText("<html><b>Director:</b> " + director + "</html>");
                yearLabel.setText("<html><b>Year:</b> " + year + "</html>");

                // Display ratings
                if (ratings != null && ratings.length() > 0) {
                    StringBuilder ratingText = new StringBuilder("<html><b>Ratings:</b><br>");
                    for (int i = 0; i < ratings.length(); i++) {
                        JSONObject rating = ratings.getJSONObject(i);
                        ratingText.append(rating.getString("Source")).append(": ").append(rating.getString("Value")).append("<br>");
                    }
                    ratingsLabel.setText(ratingText.toString() + "</html>");
                } else {
                    ratingsLabel.setText("<html><b>Ratings:</b> No ratings available.</html>");
                }

                // Display poster image
                if (!posterUrl.equals("N/A")) {
                    ImageIcon posterIcon = new ImageIcon(new URL(posterUrl));
                    posterLabel.setIcon(posterIcon);
                    viewPosterButton.setVisible(true);  // Show "View Poster" button
                } else {
                    posterLabel.setIcon(null);
                    viewPosterButton.setVisible(false);  // Hide "View Poster" button
                }

            } else {
                resultLabel.setText("Movie not found. Please try again with a different title.");
                viewPosterButton.setVisible(false);  // Hide "View Poster" button if no movie found
            }

        } catch (Exception e) {
            resultLabel.setText("Error fetching movie details: " + e.getMessage());
            viewPosterButton.setVisible(false);  // Hide "View Poster" button on error
        }
    }
}
