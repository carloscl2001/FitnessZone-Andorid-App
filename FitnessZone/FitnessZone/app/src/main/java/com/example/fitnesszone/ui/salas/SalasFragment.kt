package com.example.fitnesszone.ui.salas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesszone.databinding.FragmentSalasBinding

class SalasFragment : Fragment() {

    private var _binding: FragmentSalasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SalasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSalasBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root

    }

    // Método 'onViewCreated', que se llama después de que la vista se haya creado. Aquí se configura la lista de mascotas
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el 'RecyclerView' con un diseño lineal vertical y un adaptador vacío inicialmente
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SalasAdapter(emptyList())
        }

        // Observa el 'LiveData' en el ViewModel para recibir actualizaciones de la lista de mascotas
        // Cada vez que cambie, se actualizará el adaptador con los datos más recientes
        viewModel.salas.observe(viewLifecycleOwner) { salas ->
            (binding.recyclerView.adapter as SalasAdapter).setSalas(salas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


