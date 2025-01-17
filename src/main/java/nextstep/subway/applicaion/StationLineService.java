package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.StationLineRequest;
import nextstep.subway.applicaion.dto.StationLineResponse;
import nextstep.subway.applicaion.dto.StationSectionRequest;
import nextstep.subway.applicaion.dto.StationSectionResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationLine;
import nextstep.subway.domain.StationLineRepository;
import nextstep.subway.domain.StationSection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StationLineService {

    private final StationLineRepository stationLineRepository;
    private final StationService stationService;
    private final StationLineMapper stationLineMapper;
    private final StationSectionMapper stationSectionMapper;

    public StationLineService(StationLineRepository stationLineRepository,
                              StationService stationService,
                              StationLineMapper stationLineMapper,
                              StationSectionMapper stationSectionMapper) {
        this.stationLineRepository = stationLineRepository;
        this.stationService = stationService;
        this.stationLineMapper = stationLineMapper;
        this.stationSectionMapper = stationSectionMapper;
    }

    @Transactional
    public StationLineResponse createStationLine(StationLineRequest request) {
        StationLine stationLine = stationLineMapper.of(request);
        StationLine savedStationLine = stationLineRepository.save(stationLine);
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        return stationLineMapper.of(savedStationLine, upStation, downStation);
    }

    @Transactional(readOnly = true)
    public List<StationLineResponse> getStationLines() {
        List<StationLine> stationLines = stationLineRepository.findAll();
        return stationLines.stream()
                .map(savedStationLine -> stationLineMapper.of(savedStationLine, findLineStations(savedStationLine)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationLineResponse getStationLine(Long lineId) {
        StationLine stationLine = findLineById(lineId);
        return stationLineMapper.of(stationLine, findLineStations(stationLine));
    }

    @Transactional
    public void updateStationLine(Long lineId, StationLineRequest request) {
        StationLine stationLine = findLineById(lineId);
        stationLine.changeNameAndColor(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteStationLine(Long lineId) {
        stationLineRepository.deleteById(lineId);
    }

    @Transactional
    public StationSectionResponse createStationSection(StationSectionRequest request, Long lineId) {
        validateNoneExistStation(request);
        StationSection stationSection = stationSectionMapper.of(request);
        StationLine stationLine = findLineById(lineId);
        stationLine.addSection(stationSection);
        return stationSectionMapper.of(stationSection);
    }

    @Transactional
    public void deleteStationSection(Long lineId, Long stationId) {
        StationLine stationLine = findLineById(lineId);
        StationSection section = stationLine.findSectionByDownStationId(stationId);
        stationLine.deleteSection(section);
    }

    private void validateNoneExistStation(StationSectionRequest request) {
        stationService.findStationById(request.getDownStationId());
        stationService.findStationById(request.getUpStationId());
    }

    protected StationLine findLineById(Long lineId) {
        return stationLineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("지하철노선이 존재하지 않습니다."));
    }

    private List<Station> findLineStations(StationLine stationLine) {
        return stationService.findByIdIn(stationLine.getStationIds());
    }
}