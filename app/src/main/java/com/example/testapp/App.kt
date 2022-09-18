package com.example.testapp

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.util.Base64
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class App : Application(), ViewModelStoreOwner, SavedStateRegistryOwner, LifecycleOwner {
    private val mViewModelStore = ViewModelStore()
    private val mLifecycleRegistry = LifecycleRegistry(this)
    private val mSavedStateRegistryController = SavedStateRegistryController.create(this)

    private val processLifecycleObserver = object : DefaultLifecycleObserver {
        private val SHARED_PREFERENCES_KEY = "AppSavedState"

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)

//            val serializedBundle = getSharedPreferences("data", Context.MODE_PRIVATE).getString(SHARED_PREFERENCES_KEY, "") ?: ""
            val serializedBundle = "cAIAAEJOREwBAAAAMgAAAGEAbgBkAHIAbwBpAGQAeAAuAGwAaQBmAGUAYwB5AGMAbABlAC4AQgB1\n" +
                    "AG4AZABsAGEAYgBsAGUAUwBhAHYAZQBkAFMAdABhAHQAZQBSAGUAZwBpAHMAdAByAHkALgBrAGUA\n" +
                    "eQAAAAAAAwAAAPQBAABCTkRMAgAAAFEAAABhAG4AZAByAG8AaQBkAHgALgBsAGkAZgBlAGMAeQBj\n" +
                    "AGwAZQAuAFYAaQBlAHcATQBvAGQAZQBsAFAAcgBvAHYAaQBkAGUAcgAuAEQAZQBmAGEAdQBsAHQA\n" +
                    "SwBlAHkAOgBpAG8ALgBpAG4AcgBhAGQAaQB1AHMALgBiAGUAYQB0AHIAbwBvAHQALgBBAHAAcABW\n" +
                    "AGkAZQB3AE0AbwBkAGUAbAAAAAMAAAA4AAAAQk5ETAIAAAAGAAAAdgBhAGwAdQBlAHMAAAAAAAsA\n" +
                    "AAAAAAAABAAAAGsAZQB5AHMAAAAAAAsAAAAAAAAAHQAAAGEAbgBkAHIAbwBpAGQAeAAuAHMAYQB2\n" +
                    "AGUAZABzAHQAYQB0AGUALgBSAGUAcwB0AGEAcgB0AGUAcgAAAAMAAAC4AAAAQk5ETAEAAAASAAAA\n" +
                    "YwBsAGEAcwBzAGUAcwBfAHQAbwBfAHIAZQBzAHQAbwByAGUAAAAAAAsAAAABAAAAAAAAADoAAABh\n" +
                    "AG4AZAByAG8AaQBkAHgALgBsAGkAZgBlAGMAeQBjAGwAZQAuAFMAYQB2AGUAZABTAHQAYQB0AGUA\n" +
                    "SABhAG4AZABsAGUAQwBvAG4AdAByAG8AbABsAGUAcgAkAE8AbgBSAGUAYwByAGUAYQB0AGkAbwBu\n" +
                    "AAAAAAA="
            val bundle = if (serializedBundle.isEmpty()) {
                null
            } else {
                serializedBundle.decodeFromBase64().decodeToBundle()
            }

            mSavedStateRegistryController.performRestore(bundle)

            mLifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)

            mLifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)

            mLifecycleRegistry.currentState = Lifecycle.State.RESUMED
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)

            mLifecycleRegistry.currentState = Lifecycle.State.STARTED

            val outBundle = Bundle()

            mSavedStateRegistryController.performSave(outBundle)

            val serializedBundle = outBundle.encodeToByte().encodeToBase64()
            getSharedPreferences("data", Context.MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREFERENCES_KEY, serializedBundle)
                .apply()
        }
    }

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)

        // Force Light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry
}

fun ByteArray.encodeToBase64(): String {
    return Base64.encodeToString(this, 0, this.size, 0)
}

fun ByteArray.decodeToBundle(): Bundle {
    val bundle = Bundle()
    val parcel = Parcel.obtain()

    parcel.unmarshall(this, 0, this.size)
    parcel.setDataPosition(0)

    bundle.readFromParcel(parcel)

    return bundle
}

fun String.decodeFromBase64(): ByteArray {
    return Base64.decode(this, 0)
}

fun Bundle.encodeToByte(): ByteArray {
    val parcel = Parcel.obtain()

    writeToParcel(parcel, 0)
    parcel.setDataPosition(0)

    return parcel.marshall()
}
