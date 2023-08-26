package com.appttude.h_mal.farmr.base

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelLazy
import com.appttude.h_mal.farmr.utils.displayToast
import com.appttude.h_mal.farmr.utils.getGenericClassAt
import com.appttude.h_mal.farmr.viewmodel.ApplicationViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

abstract class BaseActivity<V : BaseViewModel> : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory by instance<ApplicationViewModelFactory>()

    val viewModel: V by getViewModel()

    private fun getViewModel(): Lazy<V> =
        ViewModelLazy(getGenericClassAt(0), storeProducer = { viewModelStore },
            factoryProducer = { factory } )


    /**
     *  Creates a loading view which to be shown during async operations
     *
     *  #setOnClickListener(null) is an ugly work around to prevent under being clicked during
     *  loading
     */

    fun <A : AppCompatActivity> startActivity(activity: Class<A>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    /**
     *  Called in case of success or some data emitted from the liveData in viewModel
     */
    open fun onStarted() {}

    /**
     *  Called in case of success or some data emitted from the liveData in viewModel
     */
    open fun onSuccess(data: Any?) {}

    /**
     *  Called in case of failure or some error emitted from the liveData in viewModel
     */
    open fun onFailure(error: Any?) {
        if (error is String) displayToast(error)
    }

    fun setTitleInActionBar(title: String) {
        supportActionBar?.title = title
    }
}