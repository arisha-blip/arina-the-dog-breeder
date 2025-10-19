package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        // TODO Task 1: Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            // Check for HTTP-level errors (e.g., 404 Not Found)
            if (!response.isSuccessful()) {
                throw new BreedNotFoundException("API call failed with code: " + response.code() + " for breed: " + breed);
            }

            // Get the response body as a string
            String responseBody = response.body().string();
            if (responseBody == null) {
                throw new BreedNotFoundException("API returned an empty body for breed: " + breed);
            }

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(responseBody);

            String status = jsonResponse.getString("status");
            if (!"success".equals(status)) {
                String apiMessage = jsonResponse.optString("message", "Unknown API error");
                throw new BreedNotFoundException("API error for breed '" + breed + "': " + apiMessage);
            }

            JSONArray subBreedsArray = jsonResponse.getJSONArray("message");
            List<String> subBreedsList = new ArrayList<>();

            // Convert the JSONArray to a List<String>
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreedsList.add(subBreedsArray.getString(i));
            }

            return subBreedsList;

        } catch (IOException | org.json.JSONException e) {
            // Catch network errors (IOException) or JSON parsing errors (JSONException)
            // As per requirements, report all failures as BreedNotFoundException
            throw new BreedNotFoundException("Failed to fetch or parse sub-breeds for: " + breed, e);
        }
    }
}
