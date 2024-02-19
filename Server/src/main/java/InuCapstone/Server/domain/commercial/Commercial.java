package InuCapstone.Server.domain.commercial;

import InuCapstone.Server.domain.favorite.Favorite;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Commercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commercial_id")
    private Long commercialId;

    @Column(name = "dong",nullable = false)
    private String dong;

    @Column(name = "quarter",nullable = false)
    private Long quarter;

    @Column(name = "type",nullable = false)
    private String type;

    @Column(name = "monthly_sales",nullable = false)
    private Long monthlySales;

    @Column(name = "weekday_sales",nullable = false)
    private Long weekdaySales;

    @Column(name = "weekend_sales",nullable = false)
    private Long weekendSales;

    @Column(name = "daily_sales",nullable = false)
    private Long dailySales;

    @Column(name = "sales_by_time",nullable = false)
    private Long salesByTime;

    @Column(name = "sales_by_age",nullable = false)
    private Long salesByAge;

    @Column(name = "sales_by_men",nullable = false)
    private Long salesByMen;

    @Column(name = "sales_by_women",nullable = false)
    private Long salesByWomen;

    @OneToMany(mappedBy = "commercial", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Favorite> favorites;
}
