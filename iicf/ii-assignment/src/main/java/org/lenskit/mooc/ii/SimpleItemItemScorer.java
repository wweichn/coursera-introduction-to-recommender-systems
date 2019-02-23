package org.lenskit.mooc.ii;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.ratings.Rating;
import org.lenskit.results.Results;
import org.lenskit.util.ScoredIdAccumulator;
import org.lenskit.util.TopNScoredIdAccumulator;
import org.lenskit.util.math.Vectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemScorer extends AbstractItemScorer {
    private final SimpleItemItemModel model;
    private final DataAccessObject dao;
    private final int neighborhoodSize;

    @Inject
    public SimpleItemItemScorer(SimpleItemItemModel m, DataAccessObject dao) {
        model = m;
        this.dao = dao;
        neighborhoodSize = 20;
    }

    /**
     * Score items for a user.
     * @param user The user ID.
     * @param items The score vector.  Its key domain is the items to score, and the scores
     *               (rating predictions) should be written back to this vector.
     */
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items) {
        Long2DoubleMap itemMeans = model.getItemMeans();
        Long2DoubleMap ratings = getUserRatingVector(user);

        // TODO Normalize the user's ratings by subtracting the item mean from each one.
        
        for(Map.Entry<Long, Double> e: ratings.entrySet())
        {
        		e.setValue(e.getValue() - itemMeans.get(e.getKey()));
        }
    
        List<Result> results = new ArrayList<>();
        myComparator mc = new myComparator();
        double score = 0;
        for (long item: items ) {
            // TODO Compute the user's score for each item, add it to results
        	   
        		double nominator = 0;
            double denominator = 0;
            PriorityQueue<Item> minheap = new PriorityQueue<Item>(neighborhoodSize,mc);
        		Long2DoubleMap allNeighbors = model.getNeighbors(item);
        		for(Map.Entry<Long, Double> e: allNeighbors.entrySet())
        		{
        			if(ratings.containsKey(e.getKey()) && (e.getKey() != item))
        			{
        				if(minheap.size() < neighborhoodSize)
        				{
        					minheap.add(new Item(e.getKey(),e.getValue()));
        				}
        				else if(minheap.peek().cosine < e.getValue())
        				{
        					minheap.poll();
        					minheap.add(new Item(e.getKey(), e.getValue()));
        				}
        			}
        		}
        		for(Item it:minheap)
        		{
        			System.out.println("ok" + it.cosine + ratings.get(it.id));
        			denominator += it.cosine;
        			nominator += it.cosine * ratings.get(it.id);
        		}

        		score = nominator / denominator + itemMeans.get(item);  // user for this item's rating
        		results.add(Results.create(item, score));
        }

        return Results.newResultMap(results);

    }
    class Item{
		Long id;
		Double cosine;
		public Item(Long id, Double cosine)
		{
			this.id = id;
			this.cosine = cosine;
		}
	}
    class myComparator implements Comparator<Item>
    {
    	public int compare(Item n1, Item n2)
    	{
    		return n1.cosine > n2.cosine? 1:-1;
    	}
    }
    

    /**
     * Get a user's ratings.
     * @param user The user ID.
     * @return The ratings to retrieve.
     */
    private Long2DoubleOpenHashMap getUserRatingVector(long user) {
        List<Rating> history = dao.query(Rating.class)
                                  .withAttribute(CommonAttributes.USER_ID, user)
                                  .get();

        Long2DoubleOpenHashMap ratings = new Long2DoubleOpenHashMap();
        for (Rating r: history) {
            ratings.put(r.getItemId(), r.getValue());
        }

        return ratings;
    }
 }
