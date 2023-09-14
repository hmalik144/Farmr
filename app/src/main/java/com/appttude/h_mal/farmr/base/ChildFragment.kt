package com.appttude.h_mal.farmr.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import com.appttude.h_mal.farmr.model.ViewState
import com.appttude.h_mal.farmr.utils.getGenericClassAt
import com.appttude.h_mal.farmr.utils.navigateTo
import com.appttude.h_mal.farmr.viewmodel.ApplicationViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.IOException

@Suppress("EmptyMethod", "EmptyMethod")
abstract class ChildFragment<V : BaseViewModel>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId), KodeinAware {

    override val kodein by kodein()
    private val factory by instance<ApplicationViewModelFactory>()

    lateinit var viewModel: V

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(requireParentFragment().requireParentFragment(), factory)[getGenericClassAt<V>(0).java]
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
    open fun onStarted() {}

    /**
     *  Called in case of success or some data emitted from the liveData in viewModel
     */
    open fun onSuccess(data: Any?) {}

    /**
     *  Called in case of failure or some error emitted from the liveData in viewModel
     */
    open fun onFailure(error: Any?) {}


    fun navigateParent(navArg: Any) {
        val fragment = requireParentFragment().requireParentFragment()
        when(navArg) {
            is Int -> (fragment).navigateTo(navArg)
            is NavDirections -> (fragment).navigateTo(navArg)
            else -> { throw IOException("${navArg::class} is not a valid navigation argment") }
        }
    }
}