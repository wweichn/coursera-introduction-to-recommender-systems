package org.lenskit.mooc.hybrid;

import com.google.common.base.Preconditions;
import org.lenskit.api.ItemScorer;
import org.lenskit.api.Result;
import org.lenskit.api.ResultMap;
import org.lenskit.basic.AbstractItemScorer;
import org.lenskit.bias.BiasModel;
import org.lenskit.results.Results;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Item scorer that computes a linear blend of two scorers' scores.
 *
 * <p>This scorer takes two underlying scorers and blends their scores.
 */
public class LinearBlendItemScorer extends AbstractItemScorer {
    private final BiasModel biasModel;
    private final ItemScorer leftScorer, rightScorer;
    private final double blendWeight;

    /**
     * Construct a popularity-blending item scorer.
     *
     * @param bias The baseline bias model to use.
     * @param left The first item scorer to use.
     * @param right The second item scorer to use.
     * @param weight The weight to give popularity when ranking.
     */
    @Inject
    public LinearBlendItemScorer(BiasModel bias,
                                 @Left ItemScorer left,
                                 @Right ItemScorer right,
                                 @BlendWeight double weight) {
        Preconditions.checkArgument(weight >= 0 && weight <= 1, "weight out of range");
        biasModel = bias;
        leftScorer = left;
        rightScorer = right;
        blendWeight = weight;
    }

    @Nonnull
    @Override
    public ResultMap scoreWithDetails(long user, @Nonnull Collection<Long> items) {
        List<Result> results = new ArrayList<>();

        // TODO Compute hybrid scores
        double score = 0;
        double bias = biasModel.getIntercept() + biasModel.getUserBias(user);
        double bias_ = 0;
        Map<Long, Double> scores_left = leftScorer.score(user, items);
        Map<Long, Double> scores_right = rightScorer.score(user, items);
        double s_left = 0;
        double s_right = 0;
        for(long item:items)
        {
        		bias_ = bias + biasModel.getItemBias(item);
        		s_left = 0;
        		s_right = 0;
        		if(scores_left.containsKey(item))
        			s_left = blendWeight * (scores_left.get(item) - bias_);
        		if(scores_right.containsKey(item))
        			s_right = (1 - blendWeight )* (scores_right.get(item) - bias_);
        		System.out.println("s_left" + s_left);
        		System.out.println("s_right" + s_right);
        		score = bias_ + s_left + s_right;
        		results.add(Results.create(item, score));
        		
        }

        return Results.newResultMap(results);
    }
}
