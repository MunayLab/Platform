<!DOCTYPE html>
<html lang="en-US">
<head>
  <meta name="layout" content="admin"/>
  <title>${org.nombre} - <g:message code="label.plataforma.nombre"/></title>

  <asset:javascript src="marked.js"/>
  <asset:javascript src="lodash.min.js"/>

  <g:render template="/components/forms/editor_markdown"/>
  <g:render template="/components/forms/imagen_upload"/>
  <g:render template="/components/forms/nombre_articulo"/>
  <g:render template="/components/forms/switch_button"/>
</head>
<body>
  <br>

  <g:render template="/components/panel_resumen"/>

  <g:hasErrors bean="${command}">
    <div class="alert alert-danger" role="alert">
      <g:renderErrors bean="${command}" />
    </div>
  </g:hasErrors>

  <g:hasErrors bean="${articulo}">
    <div class="alert alert-danger" role="alert">
      <g:renderErrors bean="${articulo}" />
    </div>
  </g:hasErrors>

  <div class="row">
    <div class="col-lg-12">
      <g:set var="nuevo" value="${!articulo?.id}" />
      <div class="panel panel-default">
        <div class="panel-heading">
          <h4>
            <g:if test="${nuevo}">Nuevo Articulo</g:if>
            <g:else>
              Modificar Articulo #${articulo.id}
              <g:if test="${articulo.publicado}">
                <span class="label label-success">Publicado</span>
              </g:if>
              <g:else>
                <span class="label label-warning">Borrador</span>
              </g:else>
            </g:else>
          </h4>
        </div>

        <g:form name="articulo" action="guardarArticulo" useToken="true" enctype="multipart/form-data">
          <g:if test="${!nuevo}">
            <g:hiddenField name="id" value="${articulo.id}" />
            <g:hiddenField name="autorId" value="${articulo?.autor?.id}" />
          </g:if>
          <g:else>
            <g:hiddenField name="autorId" value="${sec.loggedInUserInfo(field: 'id')}" />
          </g:else>
          <g:hiddenField name="orgId" value="${org.id}" />

          <div class="panel-body">

            <div class="form-group">
              <label for="titulo">
                <g:message code="contenido.articulo.titulo"/>*
              </label>
              <nombre-articulo name="titulo" value="${command?.titulo ?: articulo?.titulo}"
                  url="${g.createLink(absolute: true, controller:'org')}/"/>
            </div>
            <div class="form-group">
              <label for="imagen">
                <g:message code="contenido.articulo.imagen"/>
              </label>
              <imagen id="${articulo?.imagen?.id}" name="imagen" value="${articulo?.imagen?.nombre}"
                  link="${g.fileLink(file: articulo?.imagen)}"/>
            </div>
            <div class="form-group">
              <label for="etiquetas">
                <g:message code="contenido.articulo.etiquetas"/>*
              </label>
              <input type="text" class="form-control" name="palabrasClaves" value="${articulo?.palabrasClaves}"
                  placeholder="${g.message(code:'contenido.articulo.etiquetas')}"
                  required pattern=".{3,}" title="Debe contener más de 3 caracteres.">
              <p class="help-block">Debe contener más de 3 caracteres y se pueden incluir varias palabras separadas con comas.</p>
            </div>
            <div class="form-group">
              <label for="descripcion">
                <g:message code="contenido.articulo.descripcion"/>*
              </label>
              <input type="text" class="form-control" name="descripcion" value="${articulo?.descripcion}"
                  placeholder="${g.message(code:'contenido.articulo.descripcion')}"
                  required pattern=".{3,}" title="Debe contener más de 3 caracteres.">
              <p class="help-block">Debe contener más de 3 caracteres.</p>
            </div>

            <editor-markdown content="${articulo?.contenido}" name="contenido"/>

          </div>

          <div class="panel-footer">
            <div class="col-sm-6">
              <switch-button name="publicado" value="${articulo?.publicado}">
                <h5 slot="on"> Publicar Articulo</h5>
                <h5 slot="off"> Guardar Articulo</h5>
              </switch-button>
            </div>
            <div class="col-sm-6 text-right">
              <button type="submit" class="btn btn-primary">Aceptar</button>
              <g:link controller="contenido" class="btn btn-default">Cancelar</g:link>
            </div>
            <div class="clearfix"></div>
          </div>

        </g:form>

      </div>
    </div>
  </div>

</body>
</html>
