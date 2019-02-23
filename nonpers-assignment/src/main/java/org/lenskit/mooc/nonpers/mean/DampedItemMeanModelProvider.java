package org.lenskit.mooc.nonpers.mean;

import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;

import org.lenskit.baseline.MeanDamping;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.ratings.Rating;
import org.lenskit.inject.Transient;
import org.lenskit.util.io.ObjectStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provider class that builds the mean rating item scorer, computing damped item means from the
 * ratings in the DAO.
 */
public class DampedItemMeanModelProvider implements Provider<ItemMeanModel> {
    /**
     * A logger that you can use to emit debug messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(DampedItemMeanModelProvider.class);

    /**
     * The data access object, to be used when computing the mean ratings.
     */
    private final DataAccessObject dao;
    /**
     * The damping factor.
     */
    private final double damping;

    /**
     * Constructor for the mean item score provider.
     *
     * <p>The {@code @Inject} annotation tells LensKit to use this constructor.
     *
     * @param dao The data access object (DAO), where the builder will get ratings.  The {@code @Transient}
     *            annotation on this parameter means that the DAO will be used to build the model, but the
     *            model will <strong>not</strong> retain a reference to the DAO.  This is standard procedure
     *            for LensKit models.
     * @param damping The damping factor for Bayesian damping.  This is number of fake global-mean ratings to
     *                assume.  It is provided as a parameter so that it can be reconfigured.  See the file
     *                {@code damped-mean.groovy} for how it is used.
     */
    @Inject
    public DampedItemMeanModelProvider(@Transient DataAccessObject dao,
                                       @MeanDamping double damping) {
        this.dao = dao;
        this.damping = damping;
    }

    /**
     * Construct an item mean model.
     *
     * <p>The {@link Provider#get()} method constructs whatever object the provider class is intended to build.</p>
     *
     * @return The item mean model with mean ratings for all items.
     */
    @Override
    public ItemMeanModel get() {
        // TODO Compute damped means
        // TODO Remove the line below when you have finished
    	     logger.debug("compute damped mean for all movies");
    	     
    	     double globalmean = 0;
    	     double globalusers = 0;
    	     
    	     Long2DoubleOpenHashMap movies_ratings = new Long2DoubleOpenHashMap();
         Long2DoubleOpenHashMap user_counts = new Long2DoubleOpenHashMap();
         
         try (ObjectStream<Rating> ratings = dao.query(Rating.class).stream()) {
             for (Rating r: ratings) {
                 // this loop will run once for each rating in the data set
                 // TODO process this rating
            	 	globalmean += r.getValue();
            	 	globalusers ++;
                 if(!movies_ratings.containsKey(r.getItemId()))
                 {
                     movies_ratings.put(r.getItemId(),r.getValue());
                     user_counts.put(r.getItemId(),1.0);
                 }
                 else
                 {
                     movies_ratings.addTo(r.getItemId(),r.getValue());
                     user_counts.addTo(r.getItemId(),1);
                 }
             }
         }
         globalmean = globalmean / globalusers;

         Long2DoubleOpenHashMap dampedmeans = new Long2DoubleOpenHashMap();
         // TODO Finalize means to store them in the mean model
         LongIterator it = movies_ratings.keySet().iterator();
         while(it.hasNext())
         {
         		long key = (long)it.next();
         		double v = (movies_ratings.get(key) + damping * globalmean) / (user_counts.get(key) + damping);
         		dampedmeans.put(key, v);
         }

         logger.info("computed mean ratings for {} items", dampedmeans.size());
         return new ItemMeanModel(dampedmeans);
     }
        //throw new UnsupportedOperationException("damped mean not implemented");
    }
