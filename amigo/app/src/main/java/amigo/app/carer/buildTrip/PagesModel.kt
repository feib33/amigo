package amigo.app

// Model for Fragment Pages in custom Build Trip View Pager

enum class PagesModel(val order: Int, val pageID: Int, val title: String) {
    LOCATION(0, R.layout.layout_location, "Location"),
    DESTINATION(1, R.layout.layout_destination, "Destination"),
    TRANSPORT(2, R.layout.layout_transport, "Transport");

    // returns enum from integer relating to their 'order'
    companion object {
        private val map = PagesModel.values().associateBy(PagesModel::order)
        fun fromInt(type: Int) = map[type] ?: LOCATION
    }

}
