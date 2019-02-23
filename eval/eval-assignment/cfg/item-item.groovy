for (nnbrs in [5, 10, 15, 20, 25, 30, 40, 50, 75, 100]) {
algorithm("ItemItem") {
    attributes["NNbrs"] = nnbrs
    include 'fallback.groovy'
    bind ItemScorer to ItemItemScorer
    bind VectorSimilarity to CosineVectorSimilarity
    set NeighborhoodSize to nnbrs
    bind UserVectorNormalizer to BiasModelUserVectorNormalizer
    within (UserVectorNormalizer) {
        bind BiasModel to UserBiasModel
        }
    }
algorithm("ItemItemNorm") {
    attributes["NNbrs"] = nnbrs
    include 'fallback.groovy'
    bind ItemScorer to ItemItemScorer
    bind VectorSimilarity to CosineVectorSimilarity
    set NeighborhoodSize to nnbrs
    bind UserVectorNormalizer to BiasModelUserVectorNormalizer
    within (UserVectorNormalizer) {
        bind BiasModel to ItemBiasModel
        }
    }
 }  
    