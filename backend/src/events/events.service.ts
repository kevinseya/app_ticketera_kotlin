import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateEventDto } from './dto/create-event.dto';
import { UpdateEventDto } from './dto/update-event.dto';

@Injectable()
export class EventsService {
  constructor(private prisma: PrismaService) {}

  async create(createEventDto: CreateEventDto) {
    const sanitizedImageUrl = createEventDto.imageUrl
      ? createEventDto.imageUrl.replace(/\u0000/g, '').trim()
      : undefined;

    const event = await this.prisma.event.create({
      data: {
        ...createEventDto,
        imageUrl: sanitizedImageUrl,
        date: new Date(createEventDto.date),
        totalSeats: 100, // 10x10
      },
    });

    // Crear los asientos (10x10 = 100 asientos)
    const seats: Array<{
      eventId: string;
      row: number;
      column: number;
      isOccupied: boolean;
    }> = [];
    for (let row = 1; row <= 10; row++) {
      for (let column = 1; column <= 10; column++) {
        seats.push({
          eventId: event.id,
          row,
          column,
          isOccupied: false,
        });
      }
    }

    await this.prisma.seat.createMany({
      data: seats,
    });

    return this.findOne(event.id);
  }

  async findAll() {
    return this.prisma.event.findMany({
      orderBy: { date: 'asc' },
      include: {
        _count: {
          select: {
            seats: {
              where: { isOccupied: false },
            },
          },
        },
      },
    });
  }

  async findOne(id: string) {
    const event = await this.prisma.event.findUnique({
      where: { id },
      include: {
        seats: {
          orderBy: [{ row: 'asc' }, { column: 'asc' }],
        },
        _count: {
          select: {
            tickets: true,
          },
        },
      },
    });

    if (!event) {
      throw new NotFoundException('Evento no encontrado');
    }

    return event;
  }

  async update(id: string, updateEventDto: UpdateEventDto) {
    const event = await this.prisma.event.findUnique({ where: { id } });

    if (!event) {
      throw new NotFoundException('Evento no encontrado');
    }

    const dataToUpdate: any = { ...updateEventDto };
    if (updateEventDto.date) {
      dataToUpdate.date = new Date(updateEventDto.date);
    }
    if (typeof updateEventDto.imageUrl === 'string') {
      dataToUpdate.imageUrl = updateEventDto.imageUrl.replace(/\u0000/g, '').trim();
    }

    return this.prisma.event.update({
      where: { id },
      data: dataToUpdate,
      include: {
        seats: {
          orderBy: [{ row: 'asc' }, { column: 'asc' }],
        },
      },
    });
  }

  async remove(id: string) {
    const event = await this.prisma.event.findUnique({ where: { id } });

    if (!event) {
      throw new NotFoundException('Evento no encontrado');
    }

    await this.prisma.event.delete({ where: { id } });
    
    return { message: 'Evento eliminado exitosamente' };
  }

  async getAvailableSeats(eventId: string) {
    const event = await this.prisma.event.findUnique({ where: { id: eventId } });

    if (!event) {
      throw new NotFoundException('Evento no encontrado');
    }

    return this.prisma.seat.findMany({
      where: {
        eventId,
        isOccupied: false,
      },
      orderBy: [{ row: 'asc' }, { column: 'asc' }],
    });
  }
}
