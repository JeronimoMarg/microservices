import './Notificacion.css';

const Notificacion = ({mensaje, tipo}) => {

    if(!mensaje){
        return null;
    }

    return (
        <div className="notificacion-container">
            <p className={tipo}>
                {mensaje}
            </p>
        </div>
    )
}

export default Notificacion