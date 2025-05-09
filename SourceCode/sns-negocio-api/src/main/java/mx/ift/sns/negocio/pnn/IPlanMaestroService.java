package mx.ift.sns.negocio.pnn;

import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;

public interface IPlanMaestroService {

    PlanMaestroDetalle getPlanMaestroDetalle(Long numeroInicial, Long numeroFinal);

    void syncPlanMaestroAsync();

}