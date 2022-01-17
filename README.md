# tweets
Tweets API as AWS Lambda with API Gateway and DynamoDB

Instructions for build
This is a multi module maven project and all the AWS Lambdas are built using the maven shade plugin

build command 
```Shell
mvn clean package shade:shade
```

## Testing Endpoints

### Login

Curl command to test the login
```Shell
curl -X POST -H "Content-Type: application/json"  -d '{"Id": "123459","user": {"userName": "user4","password":"<password_here>"}' \
https://0tg9vck032.execute-api.eu-west-2.amazonaws.com/test/login`

Example Respone

`{
  "token": eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidXNlcjMiLCJpc3MiOiJtYWRodXN1ZGhhbiIsImF1ZCI6InRlc3QgYXVkaWVuY2UiLCJleHAiOjE2NDI0MTM5NTR9.mEMnKaF2GUkA4zGhG7dVwACk_Tvc3HSDTjJJPxQoAKE"
 }
 ```

### Create
Curl command to test the endpoint
```Shell
curl -X PUT -H "Content-Type: application/json"  -H "Authorization: Bearer <token goes here>" -d '{"Id": "<new_id_here>","user": {"userName": "user4","verified": false,"userId": "user4","description": "user4","followersCount": 123},
    "text": "New tweet alert for user4", \
    "favorite": true, \
    "retweeted": false, \
    "entities": { \
        "hashtags": [ \
            "#tweettweet" \
        ], \
        "URLs": [ \
            "https://exampleurl.madhu" \
        ] \
    } \
}' \
https://0tg9vck032.execute-api.eu-west-2.amazonaws.com/test/tweets/create
```

Example Response 
```Shell
{
    "message": "New tweet created"
}
```


## Get Tweets
GetAllTweets
Curl command to test the endpoint
```Shell
curl -X GET -H "Content-Type: application/json"  -H "Authorization: Bearer <token goes here>"  https://0tg9vck032.execute-api.eu-west-2.amazonaws.com/test/tweets
```

Example Response
```Shell
{
    "tweetCount": 5,
    "tweets": "[{\"id\":\"123456\",\"user\":{\"description\":\"user1\",\"followersCount\":123,\"userId\":\"user1\",\"userName\":\"user1\",\"verified\":false},\"favorite\":false,\"retweeted\":false,\"text\":\"My first tweet!!!\"},{\"id\":\"123458\",\"user\":{\"description\":\"user1\",\"followersCount\":123,\"userId\":\"user1\",\"userName\":\"user1\",\"verified\":false},\"entities\":{},\"favorite\":true,\"retweeted\":true,\"text\":\"New tweet alert again\"},{\"id\":\"123459\",\"user\":{\"description\":\"user4\",\"followersCount\":123,\"userId\":\"user4\",\"userName\":\"user4\",\"verified\":false},\"entities\":{},\"favorite\":true,\"retweeted\":false,\"text\":\"New tweet alert for user4\"},{\"id\":\"123457\",\"user\":{\"description\":\"user1\",\"followersCount\":123,\"userId\":\"user1\",\"userName\":\"user1\",\"verified\":false},\"entities\":{},\"favorite\":false,\"retweeted\":false,\"text\":\"New tweet alert\"},{\"id\":\"123460\",\"user\":{\"description\":\"user3\",\"followersCount\":1234,\"userId\":\"user3\",\"userName\":\"user3\",\"verified\":false},\"entities\":{},\"favorite\":true,\"retweeted\":false,\"text\":\"New tweet alert for user3\"}]"
}
```

GetTweetsById
Curl command to test the endpoint
```Shell
curl -X GET -H "Content-Type: application/json"  -H "Authorization: Bearer <token goes here>"  https://0tg9vck032.execute-api.eu-west-2.amazonaws.com/test/tweets/{id}
```
Example Respone

```Shell
{
    "Tweet": {
        "id": "123460",
        "user": {
            "description": "user3",
            "followersCount": 1234,
            "userId": "user3",
            "userName": "user3",
            "verified": false
        },
        "entities": {},
        "favorite": true,
        "retweeted": false,
        "text": "New tweet alert for user3"
    }
}
```








