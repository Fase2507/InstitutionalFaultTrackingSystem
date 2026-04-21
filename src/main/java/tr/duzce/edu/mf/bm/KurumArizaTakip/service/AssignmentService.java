package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.springframework.stereotype.Service;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.TechnicianProfile;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.TechnicianProfileDao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final TechnicianProfileDao technicianProfileDao;

    public AssignmentService(TechnicianProfileDao technicianProfileDao) {
        this.technicianProfileDao = technicianProfileDao;
    }

    public Optional<TechnicianProfile> pickTechnicianForCategory(Long categoryId) {
        List<TechnicianProfile> candidates = technicianProfileDao.findActiveByCategoryId(categoryId);
        return candidates.stream()
                .min(Comparator.comparing(tp -> tp.getUser().getId()));
    }
}

