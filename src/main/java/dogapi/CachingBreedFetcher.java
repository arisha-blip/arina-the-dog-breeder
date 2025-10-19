package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher underlyingFetcher;

    private final Map<String, List<String>> cache;

    private int callsMade = 0;

    /**
     * Constructs a new CachingBreedFetcher that wraps the given fetcher.
     * @param fetcher The underlying BreedFetcher to use for actual data retrieval.
     */
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlyingFetcher = fetcher;
        this.cache = new HashMap<>();
    }

    /**
     * Gets the list of sub-breeds for a given breed.
     * It first checks the cache. If the breed is not cached, it delegates
     * to the underlying fetcher and caches the result on success.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the underlying fetcher fails to find the breed
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Check the cache first (Cache Hit)
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // If not in cache (Cache Miss), call the underlying fetcher
        try {
            // Increment the counter because actually making a call
            callsMade++;
            List<String> subBreeds = underlyingFetcher.getSubBreeds(breed);

            // if successful, store the result in the cache
            cache.put(breed, subBreeds);

            // Return the new result
            return subBreeds;

        } catch (BreedNotFoundException e) {
            throw e;
        }
    }

    /**
     * Gets the number of calls made to the underlying fetcher.
     * @return the total number of calls
     */
    public int getCallsMade() {
        return callsMade;
    }
}