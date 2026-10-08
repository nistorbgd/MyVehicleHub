package com.nst.myvehiclehub.entity;

import com.nst.myvehiclehub.enums.HistoryEntryType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "history_entries")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryEntryRecord {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "cost")
  private Double cost;

  @Column(name = "serviceTime")
  private LocalDateTime serviceTime;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private HistoryEntryType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vehicle_id", nullable = false)
  private VehicleRecord vehicle;
}
