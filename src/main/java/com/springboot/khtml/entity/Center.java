package com.springboot.khtml.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="CENTER_ID")
    private Long id;

    private String region;
    private String district;
    private String center_name;
    private String designated_beds;
    private String contact_number;

    @OneToMany(mappedBy = "center")
    private List<User> users = new ArrayList<>();
}
