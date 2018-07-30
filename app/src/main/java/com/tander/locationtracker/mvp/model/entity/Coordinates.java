package com.tander.locationtracker.mvp.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.tander.locationtracker.room.ProviderConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(indices = {@Index(value = {"timestamp"})})
// Since we don't implement clean architecture, this entity serves as Room Entity
public class Coordinates {

    private double latitude;

    private double longitude;

    @TypeConverters({ProviderConverter.class})
    private CoordinatesProvider coordinatesProvider;

    @PrimaryKey
    private long timestamp;
}
