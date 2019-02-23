package org.lenskit.mooc.hybrid;

import it.unimi.dsi.fastutil.longs.LongSet;

import org.apache.commons.math3.linear.RealVector;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.bias.BiasModel;
import org.lenskit.bias.UserBiasModel;
import org.lenskit.data.ratings.RatingSummary;
import org.lenskit.results.Results;
import org.lenskit.util.collections.LongUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Item scorer that does a logistic blend of a subsidiary item scorer and popularity.  It tries to predict
 * whether a user has rated a particular item.
 */

/*

class MyResultMap implements ResultMap {
	Map<Long, Result> m;
	
	public MyResultMap() {
		m = new HashMap<>();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return m.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Result get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result put(Long key, Result value) {
		// TODO Auto-generated method stub
		m.put(key, value);
		return value;
	}

	@Override
	public Result remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends Long, ? extends Result> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Result> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<Long, Result>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Result> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, Double> scoreMap() {
		// TODO Auto-generated method stub]
		Map<Long, Double> sm = new HashMap<>();
		for (Long key : m.keySet()) {
			sm.put(key, m.get(key).getScore());
		}
		return sm;
	}

	@Override
	public Result get(long key) {
		// TODO Auto-generated method stub
		return m.getOrDefault(key, Results.create(key, 0));
	}

	@Override
	public double getScore(long id) {
		// TODO Auto-generated method stub
		double score = 0;
		if (m.containsKey(id)) {
			score = m.get(id).getScore();
		}
		return score;
	}
	
}
*/
public class LogisticItemScorer extends AbstractItemScorer {
    private final LogisticModel logisticModel;
    private final BiasModel biasModel;
    private final RecommenderList recommenders;
    private final RatingSummary ratingSummary;

    @Inject
    public LogisticItemScorer(LogisticModel model, UserBiasModel bias, RecommenderList recs, RatingSummary rs) {
        logisticModel = model;
        biasModel = bias;
        recommenders = recs;
        ratingSummary = rs;
    }

    @Nonnull
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items) {
        // TODO Implement item scorer
    		List<Result> results = new ArrayList<>();
        int parameterCount = recommenders.getRecommenderCount();
    	    double[] variables = new double[parameterCount + 2];
    	    List<ItemScorer> itemscorers = recommenders.getItemScorers();
    	    for(Long item:items)
    	    {
    	    	double score = 0;
    	    	variables[1] = Math.log10(ratingSummary.getItemRatingCount(item));variables[0] = biasModel.getIntercept() + biasModel.getItemBias(item) + biasModel.getUserBias(user);
        		int j = 2;
        		for(ItemScorer scorer:itemscorers)
        		{
        			 if(scorer.score(user, item) != null)
 			    		variables[j] = scorer.score(user, item).getScore() - variables[0];
 			     else
 			    		variables[j] = 0;
        			 j += 1;	
        		}
        		RealVector a = logisticModel.getCoefficients();
        		System.out.println(a.getDimension());
        		score = logisticModel.getIntercept() + a.getEntry(0) * variables[0] + a.getEntry(1) * variables[1] + a.getEntry(2) * variables[2];
        		//score += logisticModel.evaluate(1, variables);    	
        		System.out.println("score" + score);
        		results.add(Results.create(item, LogisticModel.sigmoid(score)));
        		
    	    }
     //   throw new UnsupportedOperationException("item scorer not implemented");
    	    return Results.newResultMap(results);
    } 
    
    
    
}
