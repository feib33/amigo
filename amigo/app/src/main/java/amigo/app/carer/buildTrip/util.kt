package amigo.app.carer.buildTrip

import android.content.Context
import android.widget.Toast

// utility function for making toasts
fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()