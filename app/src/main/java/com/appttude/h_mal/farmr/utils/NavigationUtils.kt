package com.appttude.h_mal.farmr.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.ChildFragment

fun Fragment.navigateToFragment(newFragment: Fragment) {
    childFragmentManager.beginTransaction()
        .add(R.id.container, newFragment)
        .commit()
}


fun View.navigateTo(navigationId: Int) {
    Navigation.findNavController(this).navigate(navigationId)
}

fun View.navigateTo(navDirections: NavDirections) {
    Navigation.findNavController(this).navigate(navDirections)
}

fun Fragment.navigateTo(navigationId: Int) {
    Navigation.findNavController(requireView()).navigate(navigationId)
}

fun Fragment.navigateTo(navDirections: NavDirections) {
    Navigation.findNavController(requireView()).navigate(navDirections)
}

fun Fragment.goBack() = Navigation.findNavController(requireView()).popBackStack()