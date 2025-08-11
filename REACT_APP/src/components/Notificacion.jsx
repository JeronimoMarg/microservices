
const Notificacion = ({mensaje, tipo}) => {

    if(!mensaje){
        return null;
    }

    return (
        <p className={tipo}>
            {mensaje}
        </p>
    )
}

export default Notificacion