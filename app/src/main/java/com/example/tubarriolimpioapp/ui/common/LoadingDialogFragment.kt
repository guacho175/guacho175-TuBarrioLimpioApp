package com.example.tubarriolimpioapp.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.tubarriolimpioapp.R

class LoadingDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No se puede cancelar tocando fuera
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.ThemeOverlay_TuBarrio_LoadingDialog)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCanceledOnTouchOutside(false)

        // Ajustamos tamaño al contenido
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvMessage: TextView? = dialog?.findViewById(R.id.tvLoadingMessage)
        val message = arguments?.getString(ARG_MESSAGE)
        if (!message.isNullOrEmpty()) {
            tvMessage?.text = message
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Importante devolver la vista del layout
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

    companion object {
        private const val ARG_MESSAGE = "ARG_MESSAGE"

        fun newInstance(message: String? = null): LoadingDialogFragment {
            val fragment = LoadingDialogFragment()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }
}
