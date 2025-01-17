package nextstep.subway.applicaion.dto;

public class StationSectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public StationSectionResponse(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
