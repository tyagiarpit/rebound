package io.pileworx.rebound.application

import io.pileworx.rebound.application.assembler.MockAssembler
import io.pileworx.rebound.application.dto.MockDto
import io.pileworx.rebound.common.velocity.TemplateEngine
import io.pileworx.rebound.domain.{Mock, MockRepository}
import io.pileworx.rebound.domain.command.DefineMockCmd
import io.pileworx.rebound.domain.mock.{MockId, Response}

class ReboundService(repository: MockRepository,
                     engine: TemplateEngine,
                     assembler: MockAssembler) {
  def findAll(): List[MockDto] = assembler.toDtos(repository.findAll())
  def add(cmd: DefineMockCmd): Unit = repository.save(Mock(cmd, engine))
  def clear(): Unit = repository.reset()
  def nextResponseById(id: MockId): Option[Response] = repository.findById(id) match {
    case Some(m) => m.nextResponse()
    case None => repository.findByUriAndMethod(id)
      .find(m => (m.id.query.isEmpty || m.id.query.equals(id.query))
              && (m.id.headers.isEmpty || m.id.headers.get.isEmpty || m.id.headers.equals(id.headers))) match {
      case Some(m1) => m1.nextResponse()
      case None => None
    }
  }
}