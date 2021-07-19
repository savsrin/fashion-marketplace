
# Sustainable Fashion Market 

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app provides a streamlined, fashion-focused matching process and platform for people to buy and sell sustainable (used or handmade) clothing items. Sellers will be able to upload pictures of clothing items they wish to sell along with item metadata (e.g., item type, condition, sizing measurements, picture of seller wearing the clothing, price etc.) that will be used for the matching process. Buyers will then be able to search for a specific item and they will get matched with available items in their area based on criteria such as pricing, sizing, and item condition. If interested, a buyer can request to buy an item, and a seller can approve the request. Once successfully matched, buyers and sellers will then be able to chat to arrange item exchange and payment details. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Sustainable Fashion & Commerce
- **Mobile:** This app will make use of the mobile camera so that sellers can photograph and upload items they wish to sell; additionally, it will make use of device location to match buyers and sellers by area. 
- **Story:** Provides a convenient and streamlined platform for people to get involved in sustainable fashion/thrifting; once a variety of users are onboarded on the app, this will make opting out of the fast fashion industry a realistic option for more people since current environmentally friendly brands are prohibitively expensive. 
- **Market:** Anyone who's looking to buy/sell clothes and wants to be more environmentally friendly with their fashion choices. Ability to be matched by location, pricing, sizing, and garment type makes it a convenient and appealing option for users. 
- **Habit:** Buysrs can browse as needed for new items, and sellers can check their dashboard of items daily to see if anyone is interested in their items. Sellers will create on the app and buyers will consume on the app. 
- **Scope:** Will focus on the matching process and seller/buyer account types first, and then as time permits, will expand to include more features. The matching process and desigining user flow for both buyers/sellers is definitely a technically challenging problem. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Multiple views
* Interacts with database 
* Log in/log out 
* SDKS/APIs: Google Maps (For location matching), 
* User can search for item based on location and other parameters 
* Search results page with filter
* Buyer can initiate a transaction 
* Seller can confirm a transaction 
* Users can view and edit profiles 
* Users can upload an item with metadata to sell (camera integration)
* Users can view current items on sale + items they have bought on their dashboard
* Sign up for new user profile (both seller and buyer)
* One gesture
* One animation 
* External library for visual polish 
* Technical problem: Matching buyers and sellers together 

**Optional Nice-to-have Stories**

* Add Payment API for in house payment option 
* Add chat feature to allow users and sellers to chat within app without having to exchange contact information 
* Allow users to leave reviews for sellers
* Allow users to like + comment on items and view saved/liked items in profile 
*  Map view on home screen of sellers/items near you 


### 2. Screen Archetypes

* Login Screen
    * Login to account 
* Registration Screen
    * Create new Account 
* Home (Nav Tab)
    * Seach bar for the buyer search for an item 
    * Items near the buyer pop up if the buyer doesn't have something specific in mind 
* Search Results
    * Buyer can filter the results
    * Buyer can purchase an item 
    * Buyer can schedule a pickup time with seller 
* Profile (Nav Tab)
    * Contact Information (Public)
    * Edit Personal Information 
* Items Dashboard (Nav Tab)
    * View items you are selling 
        * refresh to know when a buyer has requested purchase, contact them 
        to confirm pickup time + share payment info
        * verify when item has been paid for & delivered to buyer 
    * View items you have bought 
        * refresh to see when item is delivered 
* Transaction Screen 
    * Submit Contact Information 
    * Choose Pickup Time 
    * (Seller will be in touch with further information)
* Creation (Nav Tab)
    * User can post new item for sale with metadata (camera integration)
* Item Details Screen 



### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home (Feed of Items) -> Filter by type, price etc. 
* Create -> Upload Item to Market
* Dashboard -> Keep track of transactions 
* Profile -> Edit Personal Info


**Flow Navigation** (Screen to Screen)

Seller / Buyer
* Login
   * -> Home
* Registration 
   * -> Home 
* Home 
   * -> Search Results Page that has filter
* Search Results Page
    * Each item -> Item details page 
    * Purchase Item -> Transaction Screen
* Transaction Screen 
    * => Dashboard 
* Creation Screen 
    * -> Sales Dashboard 
* Sales Dashboard 
    * -> Past Transactions tab 
        * Once seller has confirmed, the puchase will appear here
    * -> Pending Sales tab 
    * -> Pending Purchases tab 
        
* Profile 
    * Public Details 
    * Personal Details 
        * Edit info 
   


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>
![](https://i.imgur.com/n2pkVwd.png)



### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]

Item (for Sale)
| Property | Type | Description |
| -------- | -------- | -------- |
| objectId  | String    | unique id for item (default field)|
| seller | Pointer to User | seller of item |  
| image | File | image of garment that seller provides | 
| price | Double | list price of item| 
| color| String| color of item| 
| buy | Boolean| boolean value indicating that the item can be bought|
| rent | Boolean | boolean value indicating that the item is for rent| 
| condition| String| condition of item, i.e. "good", "fair", "like new"| 
| pickupLocation| Location| where the item can be picked up from seller| 
| description|  String| anything else the seller wishes to share about the item| 
| createdAt	| DateTime	| date when item is created (default field)
| updatedAt | 	DateTime	| date when item is last updated (default field)| 
| likesCount | Integer| number of likes for the post| 
| transaction| Pointer to Transaction
| onSale| Boolean | boolean indicating whether item has been sold


Transaction 

| Property | Type | Description |
| -------- | -------- | -------- |
| objectId  | String    | unique id for transaction (default field|
| buyer_confirmed| Boolean| boolean indicating whether buyer has confirmed purchase 
| buyer| Pointer to User| 
| seller| Pointer to User| 
|buyer_pickup_time| Time/Date| 
|buyer_contact_email| String|
|buyer_phone_number| String| 
|paid| Boolean| 
|delivered| Boolean 



User 


| Property | Type    | Description |
| -------- | -------- | -------- |
| objectId     | String     | unique id for the user (default field)     |
| name | String| name of user | 
| username | String | username of user | 
| password | String | password of user | 
| bought_items| List<Item> | list of items that the user has purchased| 
| sold_items| List<Item> | list of items that the user has for sale/sold|
| transactions| List<Transaction> | list of pending transactions  
| items_liked| List<Item>| list of items that the user has liked| 
| location| Location| current location of user| 
| size | String| "S", "M", "L" etc.| 
| waist_measurement| Double| in inches measurement of waist of user | 
| bust_measurement| Double| in inches measurement of bust of user | 
| hip_measurement | Double| in inches measurement of hip of user| 
| height | Double| in inches height of user | 


Location 


| Property |  Type | Description |
| -------- | -------- | -------- |
| lat     | Float     | latitude     |
|longitude| Float| longitude|






### Networking



- Home Feed Screen
    - [READ/GET] Query all items where buyer's criteria are met
    - [Create/POST] create new buyer request on post 
    - [Delete/POST delete new buyer request on post 
    [Create/POST] create new buyer request on post 
    - [Delete/POST delete new buyer request on post 
    - [Create/POST create new like on poast 
    [Delete/POST] delete new like on post 
    
- Create Item Screen 
    - [Create/POST] create a new item object 


- Transaction Screeen 
- Dashboard Screen 
    - [Create/Post] 
    
- Item Details Screen 
    
    
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
