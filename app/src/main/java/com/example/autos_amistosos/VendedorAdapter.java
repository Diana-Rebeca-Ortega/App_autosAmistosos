package com.example.autos_amistosos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VendedorAdapter extends RecyclerView.Adapter<VendedorAdapter.VendedorViewHolder> implements Filterable {

    private List<Vendedor> listaVendedores;
    private List<Vendedor> listaVendedoresCompleta; // Copia original para filtrar

    public VendedorAdapter(List<Vendedor> listaVendedores) {
        this.listaVendedores = listaVendedores;
        this.listaVendedoresCompleta = new ArrayList<>(listaVendedores); // Inicializa la lista completa
    }

    @NonNull
    @Override
    public VendedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendedor, parent, false);
        return new VendedorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendedorViewHolder holder, int position) {
        Vendedor vendedor = listaVendedores.get(position);

        String apellido2 = vendedor.getApellido2();
        String nombreCompleto;
        if (apellido2 == null || apellido2.isEmpty() || apellido2.equals("null")) {
            // Si el segundo apellido es nulo, vacío o se recibió como la cadena "null"
            nombreCompleto = vendedor.getNombre() + " " + vendedor.getApellido1();
        }else {
            // Si hay segundo apellido
            nombreCompleto = vendedor.getNombre() + " " + vendedor.getApellido1() + " " + apellido2;
        }
        holder.tvNombreCompleto.setText(nombreCompleto.trim() + " (ID: " + vendedor.getIdVendedor() + ")");

        // Formatear los detalles
        String detalles = String.format(Locale.US,
                "Salario: $%.2f | Comisión: %.1f %%",
                vendedor.getSalarioBase(),
                vendedor.getPorcentajeComision() * 100);

        holder.tvDetalles.setText(detalles);
    }

    @Override
    public int getItemCount() {
        return listaVendedores.size();
    }

    // Método para actualizar los datos desde la Activity (tras la llamada Volley)
    public void setListaVendedores(List<Vendedor> nuevaLista) {
        this.listaVendedores = nuevaLista;
        this.listaVendedoresCompleta = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    // Implementación del Filtro para la búsqueda en tiempo real
    @Override
    public Filter getFilter() {
        return vendedorFilter;
    }

    private Filter vendedorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Vendedor> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // Si el filtro está vacío, devuelve la lista completa
                filteredList.addAll(listaVendedoresCompleta);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();

                for (Vendedor item : listaVendedoresCompleta) {
                    // Criterio de filtrado: busca la coincidencia en Nombre, Apellido o ID
                    String nombreCompleto = (item.getNombre() + " " + item.getApellido1() + " " + item.getApellido2()).toLowerCase(Locale.getDefault());

                    if (nombreCompleto.contains(filterPattern) ||
                            String.valueOf(item.getIdVendedor()).contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Reemplazar la lista actual con la lista filtrada
            listaVendedores.clear();
            listaVendedores.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    // Clase Holder para mantener las vistas de cada elemento
    public static class VendedorViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombreCompleto;
        public TextView tvDetalles;

        public VendedorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tvNombreCompleto);
            tvDetalles = itemView.findViewById(R.id.tvDetalles);
        }
    }
}