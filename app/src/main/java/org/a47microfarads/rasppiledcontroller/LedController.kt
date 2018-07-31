package org.a47microfarads.rasppiledcontroller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_led_controller.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LedController.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LedController.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LedController : Fragment() {
    // TODO: Rename and change types of parameters

    var listener: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_red.setOnTouchListener(View.OnTouchListener {local_view, motionEvent ->
                    onButtonAction("RED", motionEvent.action)
                    return@OnTouchListener true
                })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_led_controller, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    private fun onButtonAction(color: String, action: Int) {
        val callbackHandler: LedController.OnFragmentInteractionListener? = listener as? LedController.OnFragmentInteractionListener
        callbackHandler?.onButtonAction(color,
                when (action) {
                    MotionEvent.ACTION_DOWN -> "DOWN"
                    MotionEvent.ACTION_UP -> "UP"
                    else -> "OTHERS"
                })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onButtonAction(buttonColor: String, buttonAction: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LedController.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                LedController().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
