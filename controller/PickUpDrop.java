package controller;

import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;
import model.station.AssemblyStation;
import model.station.CuttingStation;
import model.station.IngredientStorage;
import model.station.PlateStorage;
import model.station.Station;
import model.station.WashingStation;


public class PickUpDrop implements Action {

    @Override
    public boolean execute(Chef chef, Station station) {
        Item hand = chef.getHeldItem();

        // 0. Tidak ada station di depan
        if (station == null) {
            System.out.println("[PickUpDrop] Tidak ada station di depan (belum bisa drop di lantai).");
            return false;
        }

        if (station instanceof WashingStation ws) {
        // Semua drop/ambil plate di sink lewat method khusus ini
            return ws.handlePickUpDrop(chef);
        }

        if (station instanceof CuttingStation cs) {
            return cs.handlePickUpDrop(chef);   // baru
        }

        Item top = station.getItemOnStation();

        // 1. Restriksi khusus WashingStation
        if (station instanceof WashingStation) {
            if (hand instanceof Plate plate && plate.isClean()) {
                System.out.println("[PickUpDrop] Gagal: Piring bersih tidak perlu dicuci!");
                return false;
            }
        }

        // 2. Tangan kosong + ada item di station → AMBIL
        if (hand == null && top != null) {

            // Kalau IngredientStorage punya aturan khusus ngambil bahan, lempar ke interact
            if (station instanceof IngredientStorage) {
                return station.interact(chef);
            }

            chef.setHeldItem(top);
            station.setItemOnStation(null);
            System.out.println("[PickUpDrop] Chef mengambil " + top.getName()
                    + " dari " + station.getStationType());
            return true;
        }

        // 3. Tangan pegang item + station kosong → LETAKKAN
        if (hand != null && top == null) {

            // === AssemblyStation punya aturan sendiri, jangan bypass ===
            if (station instanceof AssemblyStation) {
                System.out.println("[PickUpDrop] Delegasi ke AssemblyStation.interact untuk penempatan item.");
                return station.interact(chef);
            }

            // Contoh: jangan boleh taruh sembarang item di PlateStorage
            if (station instanceof PlateStorage) {
                System.out.println("[PickUpDrop] Tidak bisa meletakkan item di PlateStorage.");
                return false;
            }

            station.setItemOnStation(hand);
            chef.setHeldItem(null);
            System.out.println("[PickUpDrop] Chef meletakkan " + hand.getName()
                    + " di " + station.getStationType());
            return true;
        }

        // 4. Case lain (dua-dua kosong / dua-dua isi) → kasih ke logic khusus station
        System.out.println("[PickUpDrop] Delegasi ke station.interact untuk kombinasi khusus.");
        return station.interact(chef);
    }
}
 