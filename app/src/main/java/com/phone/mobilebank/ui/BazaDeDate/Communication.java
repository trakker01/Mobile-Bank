//package com.phone.mobilebank.ui.BazaDeDate;
//
//import static com.mongodb.client.model.Filters.eq;
//import org.bson.Document;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//public class Communication {
//    public void GetData() {
//        // Replace the placeholder with your MongoDB deployment's connection string
//        String uri = "mongodb+srv://brogarertaster9837:2cB4Z2DO2dYeeBCD@banca.x5apjpw.mongodb.net/?retryWrites=true&w=majority";
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
//            MongoCollection<Document> collection = database.getCollection("movies");
//            Document doc = collection.find(eq("title", "Back to the Future")).first();
//            if (doc != null) {
//                System.out.println(doc.toJson());
//                //return doc.toJson().toString();
//            } else {
//                System.out.println("No matching documents found.");
//                String rez = "The data is not available";
//               // return rez.toString();
//            }
//        }
//    }
//}
