package org.lenskit.mooc.hybrid;

import org.lenskit.api.ItemScorer;
import org.lenskit.api.Result;
import org.lenskit.bias.BiasModel;
import org.lenskit.bias.UserBiasModel;
import org.lenskit.data.ratings.Rating;
import org.lenskit.data.ratings.RatingSummary;
import org.lenskit.inject.Transient;
import org.lenskit.util.ProgressLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Trainer that builds logistic models.
 */
public class LogisticModelProvider implements Provider<LogisticModel> {
    private static final Logger logger = LoggerFactory.getLogger(LogisticModelProvider.class);
    private static final double LEARNING_RATE = 0.00005;
    private static final int ITERATION_COUNT = 100;

    private final LogisticTrainingSplit dataSplit;
    private final BiasModel baseline;
    private final RecommenderList recommenders;
    private final RatingSummary ratingSummary;
    private final int parameterCount;
    private final Random random;

    @Inject
    public LogisticModelProvider(@Transient LogisticTrainingSplit split,
                                 @Transient UserBiasModel bias,
                                 @Transient RecommenderList recs,
                                 @Transient RatingSummary rs,
                                 @Transient Random rng) {
        dataSplit = split;
        baseline = bias;
        recommenders = recs;
        ratingSummary = rs;
        parameterCount = 1 + recommenders.getRecommenderCount() + 1;
        random = rng;
    }

    @Override
    public LogisticModel get() {
        List<ItemScorer> scorers = recommenders.getItemScorers();
        double intercept = 0;
        double[] params = new double[parameterCount];

        LogisticModel current = LogisticModel.create(intercept, params);

        // TODO Implement model training
        List<Rating> ratings = dataSplit.getTuneRatings();
        double[] variables = new double[parameterCount];
        for(int i = 0;i < ITERATION_COUNT;i ++)
        {
        		Collections.shuffle(ratings);
        		for(Rating rating:ratings)
        		{
        			double bias = 0;
        			variables[0] = baseline.getIntercept() + baseline.getUserBias(rating.getUserId()) + 
        					baseline.getItemBias(rating.getItemId());
        			variables[1] = Math.log10(ratingSummary.getItemRatingCount(rating.getItemId()));
        			List<ItemScorer> itemscorers = recommenders.getItemScorers();
        			int j = 0;
        			for(ItemScorer scorer:itemscorers)
        			{
        			    if(scorer.score(rating.getUserId(), rating.getItemId()) != null)
        			    		variables[j + 2] = scorer.score(rating.getUserId(), rating.getItemId()).getScore() - variables[0];
        			    else
        			    		variables[j + 2] = 0;

        				j += 1;	
        			}
        			intercept += LEARNING_RATE * 
        					rating.getValue() * current.evaluate(- rating.getValue(), variables);
        			for(int k = 0;k < parameterCount;k ++)
        			{
        				//System.out.println("ok");
        				//System.out.println(params[k]);
        				params[k] += LEARNING_RATE * rating.getValue() * variables[k] * current.evaluate(- rating.getValue(), variables);
        			}
        		}
        		current = LogisticModel.create(intercept, params);
        	
        }

        return current;
    }

}
