package org.example.moviereviewapp;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieReviewController {
    private static final String API_KEY = "8801ba0f";

    @FXML
    private TextField movieTitleField;

    @FXML
    private Label resultLabel, genreLabel, directorLabel, yearLabel, ratingsLabel;

    @FXML
    private Button viewPosterButton;

    @FXML
    private ImageView posterImageView;

    // Initialize method to hide the poster and button initially
    @FXML
    public void initialize() {
        posterImageView.setVisible(false); // Initially hide the poster
        viewPosterButton.setVisible(false); // Initially hide the view poster button
        ratingsLabel.setText("");  // Clear the rating initially
    }

    @FXML
    public void onSearchMovie() {
        String movieTitle = movieTitleField.getText().trim();
        if (!movieTitle.isEmpty()) {
            fetchMovieDetails(movieTitle);
        } else {
            resultLabel.setText("Please enter a valid movie title.");
            // Hide poster and button when no search is made
            viewPosterButton.setVisible(false);
            posterImageView.setVisible(false);
            ratingsLabel.setText("");  // Clear the rating
        }
    }

    @FXML
    public void onViewPoster() {
        // Open a new window to display the poster
        Stage posterStage = new Stage();
        posterStage.setTitle("Movie Poster");

        // Create an ImageView for the poster
        Image posterImage = posterImageView.getImage();
        if (posterImage != null) {
            ImageView posterImageViewInNewWindow = new ImageView(posterImage);

            // Adjust the size of the poster image if needed
            posterImageViewInNewWindow.setFitWidth(400);
            posterImageViewInNewWindow.setFitHeight(600);

            // Create a layout container (VBox)
            VBox vbox = new VBox(posterImageViewInNewWindow);

            // Create and set the scene
            Scene posterScene = new Scene(vbox, 400, 600);
            posterStage.setScene(posterScene);

            // Show the poster window
            posterStage.show();
        }
    }

    private void fetchMovieDetails(String movieTitle) {
        try {
            String url = "https://www.omdbapi.com/?t=" + movieTitle.replace(" ", "+") + "&apikey=" + API_KEY;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("Response") && jsonResponse.getString("Response").equals("True")) {
                String title = jsonResponse.getString("Title");
                String year = jsonResponse.getString("Year");
                String genre = jsonResponse.getString("Genre");
                String director = jsonResponse.getString("Director");
                String imdbRating = jsonResponse.optString("imdbRating", "N/A"); // Get IMDb rating
                String posterUrl = jsonResponse.getString("Poster");

                // Update the UI labels
                resultLabel.setText("Title: " + title);
                genreLabel.setText("Genre: " + genre);
                directorLabel.setText("Director: " + director);
                yearLabel.setText("Year: " + year);
                ratingsLabel.setText("IMDb Rating: " + imdbRating); // Clearly indicating IMDb rating

                // Handle the poster
                if (!posterUrl.equals("N/A")) {
                    posterImageView.setImage(new Image(posterUrl));
                    posterImageView.setVisible(false); // Keep poster hidden initially
                    viewPosterButton.setVisible(true); // Show the "View Poster" button
                } else {
                    posterImageView.setImage(null); // Clear poster
                    posterImageView.setVisible(false); // Hide poster
                    viewPosterButton.setVisible(false); // Hide the "View Poster" button
                }
            } else {
                resultLabel.setText("Movie not found. Please try again.");
                ratingsLabel.setText(""); // Clear rating
                posterImageView.setImage(null); // Clear poster
                posterImageView.setVisible(false); // Hide poster
                viewPosterButton.setVisible(false); // Hide the "View Poster" button
            }
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
            posterImageView.setVisible(false); // Hide poster on error
            viewPosterButton.setVisible(false); // Hide the "View Poster" button
            ratingsLabel.setText(""); // Clear the rating on error
        }
    }
}
