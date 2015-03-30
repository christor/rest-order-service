
Order System
============

Wednesday, March 25, 2015 9:41 AM

Create a system that manages products, inventories and orders. The system exposes 
a REST API and stores its data within a relational database, for example, MySQL.
The REST API allows consumers to perform all actions such as managing products,
setting inventories, ordering products, etc. as described in the user stories
below.

User Stories
------------

* As an administrator, I need the ability to add a product to the system
   * Done, tested
* As an administrator, I need the ability to update a product's definition in the system
   * Done, tested
* As an administrator, I need the ability to delete a product from the system
   * Done, tested
* As an administrator, I need the ability to set the initial inventory for the product in the system
   * Done, tested (with create and/or update)
* As an administrator, I need the ability to reorder products to increase the inventory level
   * If you simply want update stock level, then a PUT works
   * Instead, I created a RestockOrder...this currently is immediately effective, but could be asyncronous (need copperation from warehouse, etc)
* As a user, I need the ability to see all products in the system
   * Done, tested
* As a user, I need the ability to order one or more products for purchase
   * Done, tested
* As a user, I need the ability to specify a shipping address for my order
   * Done, tested (part of representation)
* As a user, I need the ability to specify a billing address for my order
   * Done, tested (part of representation)
* As a user, I need the ability to view my past orders
   * Done, tested
* As a user, I need the ability to search for a product
   * Done, tested

Product
-------

A PRODUCT has the following attributes 
* Product Name
* Product Description
* Number of Items in Stock
* Price per Item

Order
-----

An ORDER has the following attributes 
* Customer Name
* Customer Address
* Order Number
* Order Date
* List of Products ordered

Expectations
------------

* The system will be developed using the following technology stack
   * Java SE 1.7 or 1.8
   * Spring Framework
   * Hibernate
* You are free to choose any IDE that supports the above technology stack on an
  OS of your choice
* The database will be MySQL
  * There's a database-init.sql file to initialize this, and an sample config in application.properties that you can modify to use MySQL
* The system will expose a set of REST APIs that addresses the requirements above
* A user interface is NOT necessary
* Unit tests must be written (using either Junit or TestNG)
* The entire source code should be shared back with us
   * Please send source code or link(GitHub or Bitbucket)to prabal.ghosh@pb.com
   * The entire exercise must be completed and returned by Monday, 30th March, 2015
￼￼

Questions:

 * Do we need authentication?
   * No
 * When an order is placed, should we check stock?
   * Yes
 * How do we do restock restfully? Is that another service? I think so...
   * Ok
 * How does a user indicate they want to place an order for more than one of some product?
   * Multiple in order
 * Should it be packaged with MySQL, or simply be configurable to connect to it?
   * A script is sufficient
 * "Search for a product?" How?
   * "Don't get carried away"
 * Customer places an order, does it affect inventory?
   * Yes
 * Can we accept orders that we can't satisfy (not enough inventory, invalid items)?
   * No, it should be rejected




{
  "_links" : {
    "self" : {
      "href" : "http://localhost/products{?page,size,sort}",
      "templated" : true
    }
  },
  "_embedded" : {
    "products" : [ {
      "name" : "Widget 1",
      "description" : "The first widget in our test set",
      "stock" : 10,
      "price" : 100,
      "_links" : {
        "self" : {
          "href" : "http://localhost/products/1"
        }
      } 
    }, {
      "name" : "Widget 2",
      "description" : "The second widget in our test set",
      "stock" : 100,
      "price" : 1000,
      "_links" : {
        "self" : {
          "href" : "http://localhost/products/2"
        }
      }
    } ]
  },
  "page" : {
    "size" : 20,
    "totalElements" : 2,
    "totalPages" : 1,
    "number" : 0
  }
}

