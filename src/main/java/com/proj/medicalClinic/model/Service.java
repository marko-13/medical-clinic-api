package com.proj.medicalClinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(name = "service_type", unique = false, nullable = false)
    private String type;

    @Column(name = "price", unique = false, nullable = false)
    private double price;

    @Column(name = "deleted")
    private boolean deleted = false;

    @ManyToMany(mappedBy = "services")
    private List<Clinic> clinics; //mozda ovde treba inicijalizovati listu = new List<>(); isto i u clinic kalsi

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @ManyToMany(mappedBy = "services")
    private List<Doctor> doctors;
}
