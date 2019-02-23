for (nnbrs in [5, 10, 15, 20, 25, 30, 40, 50, 75, 100]) {
algorithm("Lucene") {
    attributes["NNbrs"] = nnbrs
    // use fallback scorer for unscorable items
    include 'fallback.groovy'
    bind ItemScorer to ItemItemScorer
    bind ItemItemModel to LuceneItemItemModel
    set NeighborhoodSize to nnbrs
}
algorithm("LuceneNorm") {
    attributes["NNbrs"] = nnbrs
    include 'fallback.groovy'
    bind ItemScorer to ItemItemScorer
    bind ItemItemModel to LuceneItemItemModel
    set NeighborhoodSize to nnbrs
    // normalize user rating vectors by subtracting biases
    bind UserVectorNormalizer to BiasUserVectorNormalizer
    // subtract the item bias (so we normalize by item mean rating)
    within (UserVectorNormalizer) {
        bind BiasModel to ItemBiasModel
        }
    }
 }