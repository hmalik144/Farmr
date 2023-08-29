package com.appttude.h_mal.farmr.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelLazy
import com.appttude.h_mal.farmr.model.ViewState
import com.appttude.h_mal.farmr.utils.getGenericClassAt
import com.appttude.h_mal.farmr.utils.popBackStack
import com.appttude.h_mal.farmr.viewmodel.ApplicationViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import kotlin.properties.Delegates

@Suppress("EmptyMethod", "EmptyMethod")
abstract class BaseFragment<V : BaseViewModel>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId), KodeinAware {

    override val kodein by kodein()
    private val factory by instance<ApplicationViewModelFactory>()

    val viewModel: V by getViewModel()

    private fun getViewModel(): Lazy<V> =
        ViewModelLazy(getGenericClassAt(0), storeProducer = { viewModelStore },
            factoryProducer = { factory } )

    var mActivity: BaseActivity? = null

    private var shortAnimationDuration by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = requireActivity() as BaseActivity
        configureObserver()
    }

    private fun configureObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is ViewState.HasStarted -> onStarted()
                is ViewState.HasData<*> -> onSuccess(it.data)
                is ViewState.HasError<*> -> onFailure(it.error)
            }
        }
    }

    /**
     *  Called in case of starting operation liveData in viewModel
     */
    open fun onStarted() {
        mActivity?.onStarted()
    }

    /**
     *  Called in case of success or some data emitted from the liveData in viewModel
     */
    open fun onSuccess(data: Any?) {
        mActivity?.onSuccess(data)
    }

    /**
     *  Called in case of failure or some error emitted from the liveData in viewModel
     */
    open fun onFailure(error: Any?) {
        mActivity?.onFailure(error)
    }

    fun setTitle(title: String) {
        (requireActivity() as BaseActivity).setTitleInActionBar(title)
    }

    fun popBackStack() = mActivity?.popBackStack()
}