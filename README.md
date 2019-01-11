# Popular Movies

Popular Movies brings in the most popular and top rated movies from TheMovieDb.org. Popular Movies displays a grid of movie posters allowing you to load movie details by tapping on a poster. This app follows material design guidelines and Udacity project guidelines.

Implementation
----
### Main
A RecyclerView with a GridLayoutManager is used to display images fetched from TheMovieDb API. Network calls are made in a background thread. A spinner is used in the toolbar to load movies either by popularity or top rated.

### Movie Details
Tapping a grid icon will load a details screen. The layout is implemented using a ConstraintLayout. A loading indicator is shown while Picasso loads an appropriately sized poster image.

Build
-----
To build this app, you'll have to add the following to your local gradle.properties:

`API_KEY="<api key>"`

Attribution
-----
Licences

- Picasso
  - [License](https://github.com/square/picasso/blob/master/LICENSE.txt)
- Butterknife
  - [License](https://github.com/JakeWharton/butterknife/blob/master/LICENSE.txt)
- Hamcrest
  - [License](http://hamcrest.org/)
