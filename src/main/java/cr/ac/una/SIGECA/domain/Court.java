package cr.ac.una.SIGECA.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "tb_courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_court")
    private Integer idCourt;
    
    @Column(name = "name_court", nullable = false)
    private String nameCourt;

    @NotBlank(message = "El tipo de cancha es obligatorio")
    @Column(name = "type_of_court", nullable = false)
    private String typeOfCourt;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(name = "location", nullable = false)
    private String location;

    @Positive(message = "El precio por hora debe ser mayor que 0")
    @Column(name = "price_by_hour", nullable = false)
    private Double priceByHour;

    @NotBlank(message = "El estado de la cancha es obligatorio")
    @Column(name = "status_court", nullable = false)
    private String statusCourt;
    
    @NotBlank(message = "El tipo de superficie es obligatorio")
    @Column(name = "surface_type", nullable = false)
    private String surfaceType;

    @Column(name = "lighting")
    private boolean lighting;

    @Column(name = "is_indoor")
    private boolean isIndoor;
  
    public Court() {
    }

    public Integer getIdCourt() {
        return idCourt;
    }

    public void setIdCourt(Integer idCourt) {
        this.idCourt = idCourt;
    } 
    
    public String getNameCourt() {
        return nameCourt;
    }

    public void setNameCourt(String nameCourt) {
        this.nameCourt = nameCourt;
    }       

    public String getTypeOfCourt() {
        return typeOfCourt;
    }

    public void setTypeOfCourt(String typeOfCourt) {
        this.typeOfCourt = typeOfCourt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPriceByHour() {
        return priceByHour;
    }

    public void setPriceByHour(Double priceByHour) {
        this.priceByHour = priceByHour;
    }    

    public String getStatusCourt() {
        return statusCourt;
    }

    public void setStatusCourt(String statusCourt) {
        this.statusCourt = statusCourt;
    }

    public String getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }

    public boolean isLighting() {
        return lighting;
    }

    public void setLighting(boolean lighting) {
        this.lighting = lighting;
    }

    public boolean isIsIndoor() {
        return isIndoor;
    }

    public void setIsIndoor(boolean isIndoor) {
        this.isIndoor = isIndoor;
    }

    @Override
    public String toString() {
        return "Court{" +
                "idCourt=" + idCourt +
                ", nameCourt='" + nameCourt + '\'' +
                ", typeOfCourt='" + typeOfCourt + '\'' +
                ", location='" + location + '\'' +
                ", priceByHour=" + priceByHour +
                ", statusCourt='" + statusCourt + '\'' +
                ", surfaceType='" + surfaceType + '\'' +
                ", lighting=" + lighting +
                ", isIndoor=" + isIndoor +
                '}';
    }
}