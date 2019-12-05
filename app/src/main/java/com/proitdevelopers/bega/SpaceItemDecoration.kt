package com.proitdevelopers.bega

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SpaceItemDecoration(mspace: Int) : RecyclerView.ItemDecoration() {

    val space = mspace

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect?.apply {

            when ((parent!!.getChildLayoutPosition(view)) % 3) {
                0,  1  -> {
                    left = space
                    right = space
                    top = space
                }

                2 -> {
                    top = space
                    right = space
                }

            }

        }

    }
}