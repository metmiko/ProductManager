## Instructions

This part of the interview process allows us to learn more about your software engineering and web development skills. Below is a description of a CRUD API that manages products, keeps track of their pricing and their view counts. You are given a boilerplate application with parts that are incomplete or not working as expected. The task is to add features to the application, adapt some parts of it and come prepared at the next interview with suggestions on how to improve it.

The boilerplate application has some basic components set up: a Product model with a database connection and an empty controller. We would like you to do the following:
- Add an API to get a single product
- Finish the implementation for fetching the currency conversion

When a single product is requested, all fields of that product are returned and the view-count for that product is incremented. The request can optionally specify a currency, in which case the price should be converted to the requested currency before being returned. We need to support the following currencies:
*	USD (default)
*	CAD
*	EUR
*	GBP

The latest exchange rates are retrieved from the public API https://currencylayer.com/. Tests are optional but we would like to hear from you how would you design such tests at the interview.
