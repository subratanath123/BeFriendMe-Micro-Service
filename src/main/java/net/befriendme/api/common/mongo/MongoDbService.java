package net.befriendme.api.common.mongo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
     * typecast from caller side if needed.
     */
    public Object getTargetAttribute(String collectionName,
                                     Criteria parentCollectionSearchCriteria,
                                     String attribute,
                                     Class<?> clazz) {

        if (clazz == null) {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(parentCollectionSearchCriteria),
                    Aggregation.project(attribute)
            );

            AggregationResults<String> results = mongoTemplate.aggregate(aggregation, collectionName, String.class);

            return results.getRawResults().get("results");
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(parentCollectionSearchCriteria),
                Aggregation.unwind(attribute),
                Aggregation.replaceRoot(attribute)
        );

        Object results = mongoTemplate.aggregate(aggregation,
                        collectionName,
                        clazz)
                .getRawResults()
                .get("results");

        if (results instanceof List<?>) {
            List<Document> listResult = (List<Document>) results;

            return listResult
                    .stream()
                    .map(object -> {
                                if (object.get("$id") != null) {
                                    return mongoTemplate.findById(object.get("$id"), clazz);
                                }

                                return object;
                            }
                    ).collect(Collectors.toList());

        } else {
            Document result = (Document) results;

            if (result.get("$id") != null) {
                return mongoTemplate.findById(result.get("$id"), clazz);
            }

            return result;
        }
    }

}
