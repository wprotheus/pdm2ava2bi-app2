package com.wprotheus.wellingtonbpneto_ranking.ui;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wprotheus.wellingtonbpneto_ranking.databinding.FragmentVisualizarBinding;
import com.wprotheus.wellingtonbpneto_ranking.model.Partida;
import com.wprotheus.wellingtonbpneto_ranking.util.RankingAdapter;

import java.util.ArrayList;
import java.util.List;

public class VisualizarFragment extends Fragment {
    private static final String PROVIDER_NAME = "com.wprotheus.wellingtonbpneto";
    private static final String URL_PARTIDA = "content://" + PROVIDER_NAME + "/partidas";
    private static final Uri CONTENT_URI = Uri.parse(URL_PARTIDA);

    private FragmentVisualizarBinding binding;
    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private List<Partida> partidaList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVisualizarBinding.inflate(inflater, container, false);
        recyclerView = binding.rvRankingViewer;
        partidaList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rankingAdapter = new RankingAdapter(partidaList);
        recyclerView.setAdapter(rankingAdapter);
        solicitarPermissoes();
        return binding.getRoot();
    }

    public void acessarDados() {
        ContentResolver resolver = requireContext().getContentResolver();
        Cursor cursor = resolver.query(CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Partida p = new Partida();
                p.setId(Integer.parseInt(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("ID")))));
                p.setNome(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
                p.setPalpite(cursor.getString(cursor.getColumnIndexOrThrow("GUESS")));
                p.setResultado(cursor.getString(cursor.getColumnIndexOrThrow("RESULT")));
                partidaList.add(p);
            }
            cursor.close();
            rankingAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(requireActivity(), "Erro ao carregar as partidas.", Toast.LENGTH_SHORT).show();
        }
    }

    public void solicitarPermissoes() {
        if (ContextCompat.checkSelfPermission(requireActivity(), "com.wprotheus.wellingtonbpneto.PERMISSION_READ")
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireActivity(), "com.wprotheus.wellingtonbpneto.PERMISSION_WRITE")
                        != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{"com.wprotheus.wellingtonbpneto.PERMISSION_READ",
                            "com.wprotheus.wellingtonbpneto.PERMISSION_WRITE"}, 100);
        else {
            acessarDados();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                acessarDados();
            else
                Toast.makeText(requireActivity(), "Permissões necessárias não foram concedidas.", Toast.LENGTH_SHORT).show();
        }
    }
}