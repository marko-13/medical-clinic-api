package com.proj.medicalClinic.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class AppUser implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, updatable = false, nullable = false)
	private Long id;

	@Version
	private Integer version;

	@Column(name = "user_role", unique = false, updatable = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleType userRole;

	@Column(name="email", unique=true, nullable=false)
	private String email;

	@Column(name="password", unique=false, nullable=false)
	private String password;

	@Column(name="name", unique=false, nullable=false)
	private String name;

	@Column(name="last_name", unique=false, nullable=false)
	private String lastName;

	@Column(name="adress", unique=false, nullable=false)
	private String adress;

	@Column(name="city", unique=false, nullable=false)
	private String city;

	@Column(name="state", unique=false, nullable=false)
	private String state;

	@Column(name="mobile", unique=false, nullable=false)
	private String mobile;

	@Column (name = "deleted")
	private boolean deleted;

	@Column(name = "enabled")
	private boolean enabled;

	@Column(name = "rejected")
	private boolean rejected;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_authority",
			joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "authority_id")}
	)
	private List<Authority> authorities;

	@Column(name = "last_password_reset_date")
	private Timestamp lastPasswordResetDate;

	@Column(name = "enabled_patient")
	private boolean enabled_patient;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public boolean getEnabledPatient() {
		return enabled_patient;
	}
}
