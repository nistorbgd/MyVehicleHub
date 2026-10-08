package com.nst.myvehiclehub.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRecord {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "make", nullable = false)
  private String make;

  @Column(name = "model", nullable = false)
  private String model;

  @Column(name = "year", nullable = false)
  private Integer year;

  @Column(name = "plateNumber", nullable = false, unique = true)
  private String plateNumber;

  @Column(name = "vin", nullable = false, unique = true)
  private String vin;

  @OneToMany(mappedBy = "vehicle")
  private List<HistoryEntryRecord> historyEntries;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUserRecord user;
}
