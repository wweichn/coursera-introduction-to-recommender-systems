package org.lenskit.mooc.uu;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMap;
import it.unimi.dsi.fastutil.longs.LongSet;

import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.ratings.Rating;
import org.lenskit.results.Results;
import org.lenskit.util.ScoredIdAccumulator;
import org.lenskit.util.TopNScoredIdAccumulator;
import org.lenskit.util.collections.LongUtils;
import org.lenskit.util.math.Scalars;
import org.lenskit.util.math.Vectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * User-user item scorer.
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleUserUserItemScorer extends AbstractItemScorer {
    private final DataAccessObject dao;
    private final int neighborhoodSize;

    /**
     * Instantiate a new user-user item scorer.
     * @param dao The data access object.
     */
    @Inject
    public SimpleUserUserItemScorer(DataAccessObject dao) {
        this.dao = dao;
        neighborhoodSize = 30;
    }

    @Nonnull
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items) {
        // TODO Score the items for the user with user-user CF
        List<Result> results = new ArrayList<>();

        LongSet users = dao.getEntityIds(CommonTypes.USER);


        Long2DoubleOpenHashMap userVector = getUserRatingVector(user);
        Long2DoubleOpenHashMap userMeanVector = meanCenterTheVector(userVector);

        double userMean = Vectors.mean(userVector);

        for(Long item :items)
        {
        		PriorityQueue<UserCosine> neighbors = neighbor(users, item, user, userMeanVector);
        		if(neighbors.size() < 2)
        		{
        			continue;
        		}
        	double score = computeScore(item, neighbors);
        	score += userMean;
        	results.add(Results.create(item, score));
        }
        
        // return final results
        return Results.newResultMap(results);
        
        //throw new UnsupportedOperationException("not yet implemented");
    }
    
    public double computeScore(Long item, PriorityQueue<UserCosine> neighbors)
    {
    		double nominator = computeNominator(neighbors,item);
    		double denominator = computeDenominator(neighbors);
    		
    		return nominator/denominator;
    }
    
    public double computeNominator(PriorityQueue<UserCosine> neighbors,Long item)
    {
    		double result = 0;
    		for(UserCosine uc: neighbors)
    		{
    			Long2DoubleOpenHashMap itRatingVector = getUserRatingVector(uc.id);
    			double rate = itRatingVector.get(item);
    			result += uc.cosine * (rate - Vectors.mean(itRatingVector));
    		}
    		return result;
    }
    
    public double computeDenominator(PriorityQueue<UserCosine> neighbors)
    {
    		double res = 0.0;
    		for(UserCosine uc:neighbors)
    		{
    			res += uc.cosine;
    		}
    		return res;
    	
    }
    
    public PriorityQueue<UserCosine> neighbor(LongSet userLongSet, Long item, Long user,Long2DoubleOpenHashMap userMeanVector) 
    {
    	    myComparator mc = new myComparator();
    	
    		PriorityQueue<UserCosine> minheap = new PriorityQueue<UserCosine>(neighborhoodSize,mc);
    		
    		for(Long other:userLongSet)
    		{
    			if(other != user)
    			{
    				Long2DoubleOpenHashMap otherVector = getUserRatingVector(other);
    				if(otherVector.containsKey(item))
    				{
    					//get cosine similarity
    					Long2DoubleOpenHashMap otherMeanVector = meanCenterTheVector(otherVector);
    					double res = Vectors.dotProduct(userMeanVector, otherMeanVector);
    					res = res / (Vectors.euclideanNorm(userMeanVector) * Vectors.euclideanNorm(otherMeanVector));
    					if(res <= 0)
    					{
    						continue;
    					}
    					UserCosine us = new UserCosine(other,res);
    					if(minheap.size() < neighborhoodSize)
    					{
    						minheap.add(us);
    					}
    					else if(minheap.peek().cosine < res)
    					{
    						minheap.poll();
    						minheap.add(us);
    					}
    				}
    			}
    		}
    		return minheap;
    				
    }
    class UserCosine{
    		Long id;
    		Double cosine;
    		public UserCosine(Long id, Double cosine)
    		{
    			this.id = id;
    			this.cosine = cosine;
    		}
    }
    class myComparator implements Comparator<UserCosine>
    {
    	public int compare(UserCosine n1, UserCosine n2)
    	{
    		return n1.cosine > n2.cosine? 1:-1;
    	}
    }

    /**
     * Get a user's rating vector.
     * @param user The user ID.
     * @return The rating vector, mapping item IDs to the user's rating
     *         for that item.
     */
    private Long2DoubleOpenHashMap meanCenterTheVector(Long2DoubleOpenHashMap ratings)
    {
    		double mean = Vectors.mean(ratings);
    		
    		Long2DoubleOpenHashMap mutUserRatingVector = new Long2DoubleOpenHashMap();
    		for(Long2DoubleMap.Entry entry:ratings.long2DoubleEntrySet())
    		{
    			mutUserRatingVector.put(entry.getLongKey(), entry.getDoubleValue() - mean);
    			
    		}
    		return mutUserRatingVector;
    }
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
